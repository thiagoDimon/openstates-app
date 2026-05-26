package com.openstates.app.service.impl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.openstates.app.dto.PoliticianPageDTO;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.service.OpenStatesApiService;
import com.openstates.app.service.PoliticianMapper;
import com.openstates.app.service.SyncExecutorService;

@ExtendWith(MockitoExtension.class)
class PoliticianServiceImplTest {

    @Mock
    private PoliticianRepository politicianRepository;
    @Mock
    private SyncExecutorService syncExecutorService;
    @Mock
    private OpenStatesApiService openStatesApiService;
    @Mock
    private PoliticianMapper politicianMapper;

    @InjectMocks
    private PoliticianServiceImpl service;

    @Test
    void findAll_withNullStateCode_returnsEmptyPage() {
        PoliticianPageDTO result = service.findAll(null, null, 0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        verify(politicianRepository, never()).countByStateCode(anyString());
    }

    @Test
    void findAll_withNoDataForState_fetchesFirstPageAndReturnsResults() {
        Politician politician = Politician.builder().id("ocd-person/1").name("Jane Doe").build();
        Page<Politician> page = new PageImpl<>(List.of(politician));

        when(politicianRepository.countByStateCode("ca")).thenReturn(0L);
        when(politicianRepository.findPageByStateCode(eq("ca"), any(Pageable.class))).thenReturn(page);
        when(politicianMapper.toDTO(politician)).thenReturn(
                new com.openstates.app.dto.PoliticianDTO("ocd-person/1", "Jane Doe", null, null, null, null, null, null,
                        null, null, List.of()));

        PoliticianPageDTO result = service.findAll("ca", null, 0, 10);

        verify(syncExecutorService).syncNextPage("ca");
        assertThat(result.content()).hasSize(1);
    }

    @Test
    void findAll_withoutPartyFilter_queriesWithoutParty() {
        Page<Politician> page = new PageImpl<>(List.of());

        when(politicianRepository.countByStateCode("ca")).thenReturn(100L);
        when(politicianRepository.findPageByStateCode(eq("ca"), any(Pageable.class))).thenReturn(page);

        service.findAll("ca", null, 0, 10);

        verify(politicianRepository).findPageByStateCode(eq("ca"), any(Pageable.class));
        verify(politicianRepository, never()).findPageByStateCodeAndParty(anyString(), anyString(),
                any(Pageable.class));
    }

    @Test
    void findAll_withPartyFilter_queriesWithParty() {
        Page<Politician> page = new PageImpl<>(List.of());

        when(politicianRepository.countByStateCode("ca")).thenReturn(100L);
        when(politicianRepository.findPageByStateCodeAndParty(eq("ca"), eq("Democratic"), any(Pageable.class)))
                .thenReturn(page);

        service.findAll("ca", "Democratic", 0, 10);

        verify(politicianRepository).findPageByStateCodeAndParty(eq("ca"), eq("Democratic"), any(Pageable.class));
        verify(politicianRepository, never()).findPageByStateCode(anyString(), any(Pageable.class));
    }

    @Test
    void syncAllFromApi_onSuccess_savesAllPoliticians() {
        OpenStatesPersonResponse response = new OpenStatesPersonResponse("ocd-person/1", "Jane Doe", null, null, null,
                null, null, null, null, null, null, null, null);
        Politician politician = Politician.builder().id("ocd-person/1").name("Jane Doe").build();

        when(openStatesApiService.fetchAllPoliticians()).thenReturn(List.of(response));
        when(politicianMapper.toEntity(response)).thenReturn(politician);

        service.syncAllFromApi();

        verify(politicianRepository).saveAll(List.of(politician));
    }
}
