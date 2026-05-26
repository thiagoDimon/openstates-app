package com.openstates.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openstates.app.dto.openstates.OpenStatesApiResponse;
import com.openstates.app.dto.openstates.OpenStatesPagination;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.entity.StateSync;
import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.repository.StateSyncRepository;

@ExtendWith(MockitoExtension.class)
class SyncExecutorServiceTest {

    @Mock
    private PoliticianRepository politicianRepository;
    @Mock
    private StateSyncRepository stateSyncRepository;
    @Mock
    private OpenStatesApiService openStatesApiService;
    @Mock
    private PoliticianMapper politicianMapper;

    @InjectMocks
    private SyncExecutorService syncExecutorService;

    @Test
    void fetchAndSavePage_withResults_savesPoliticiansAndUpdatesSync() {
        OpenStatesPersonResponse person = new OpenStatesPersonResponse(
                "ocd-person/1", "Jane Doe", null, null, null,
                null, null, null, null, null, null, null, null);

        OpenStatesApiResponse apiResponse = new OpenStatesApiResponse(List.of(person),
                new OpenStatesPagination(1, 5, 10, 50));

        Politician politician = Politician.builder().id("ocd-person/1").name("Jane Doe").build();

        when(openStatesApiService.fetchPageForState("ca", 1)).thenReturn(apiResponse);
        when(politicianMapper.toEntity(person)).thenReturn(politician);

        syncExecutorService.fetchAndSavePage("ca", 1);

        verify(politicianRepository).saveAll(List.of(politician));

        ArgumentCaptor<StateSync> captor = ArgumentCaptor.forClass(StateSync.class);
        verify(stateSyncRepository).save(captor.capture());
        assertThat(captor.getValue().getStateCode()).isEqualTo("ca");
        assertThat(captor.getValue().getLastPageFetched()).isEqualTo(1);
        assertThat(captor.getValue().getMaxPage()).isEqualTo(5);
    }

    @Test
    void fetchAndSavePage_withEmptyResults_savesNothing() {
        OpenStatesApiResponse emptyResponse = new OpenStatesApiResponse(List.of(),
                new OpenStatesPagination(1, 1, 10, 0));

        when(openStatesApiService.fetchPageForState("ca", 1)).thenReturn(emptyResponse);

        syncExecutorService.fetchAndSavePage("ca", 1);

        verify(politicianRepository, never()).saveAll(any());
        verify(stateSyncRepository, never()).save(any());
    }
}
