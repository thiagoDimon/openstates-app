package com.openstates.app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openstates.app.dto.PoliticianPageDTO;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.service.OpenStatesApiService;
import com.openstates.app.service.PoliticianMapper;
import com.openstates.app.service.PoliticianService;
import com.openstates.app.service.SyncExecutorService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoliticianServiceImpl implements PoliticianService {

    @NonNull
    private final PoliticianRepository politicianRepository;
    @NonNull
    private final SyncExecutorService syncExecutorService;
    @NonNull
    private final OpenStatesApiService openStatesApiService;
    @NonNull
    private final PoliticianMapper politicianMapper;

    @Override
    @Transactional
    public PoliticianPageDTO findAll(String stateCode, String party, int page, int size) {
        if (stateCode == null || stateCode.isBlank()) {
            return new PoliticianPageDTO(List.of(), page, size, false);
        }

        long count = politicianRepository.countByStateCode(stateCode);
        long offset = (long) page * size;

        if (offset + size >= count && syncExecutorService.hasMoreApiPages(stateCode)) {
            log.info("State {} needs more data (count={}). Fetching next API page...", stateCode, count);
            syncExecutorService.syncNextPage(stateCode);
        }

        Sort sort = Sort.by("name").ascending();
        Page<Politician> result = party != null && !party.isBlank()
                ? politicianRepository.findPageByStateCodeAndParty(stateCode, party, PageRequest.of(page, size, sort))
                : politicianRepository.findPageByStateCode(stateCode, PageRequest.of(page, size, sort));

        boolean hasNext = result.hasNext() || syncExecutorService.hasMoreApiPages(stateCode);

        return new PoliticianPageDTO(
                result.getContent().stream().map(politicianMapper::toDTO).toList(),
                page,
                size,
                hasNext
        );
    }

    @Override
    @Transactional
    public void syncAllFromApi() {
        log.info("Starting full sync from OpenStates API...");
        try {
            List<OpenStatesPersonResponse> responses = openStatesApiService.fetchAllPoliticians();
            List<Politician> politicians = responses.stream()
                    .map(politicianMapper::toEntity)
                    .toList();
            politicianRepository.saveAll(politicians);
            log.info("Full sync completed. {} politicians saved.", politicians.size());
        } catch (Exception e) {
            log.error("Sync failed: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void syncNextPageForState(String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return;
        }
        log.info("Manual sync requested for state {}...", stateCode);
        syncExecutorService.syncNextPage(stateCode);
    }
}
