package com.openstates.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openstates.app.dto.PoliticianDTO;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.entity.StateSync;
import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.repository.StateSyncRepository;
import com.openstates.app.service.OpenStatesApiService;
import com.openstates.app.service.PoliticianMapper;
import com.openstates.app.service.PoliticianService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoliticianServiceImpl implements PoliticianService {

    @NonNull private final PoliticianRepository politicianRepository;
    @NonNull private final StateSyncRepository stateSyncRepository;
    @NonNull private final OpenStatesApiService openStatesApiService;
    @NonNull private final PoliticianMapper politicianMapper;

    @Override
    @Transactional
    public List<PoliticianDTO> findAll(String stateCode, String party) {
        if (stateCode == null || stateCode.isBlank()) {
            return List.of();
        }

        boolean hasData = politicianRepository.existsByRoles_StateCode(stateCode);

        if (!hasData) {
            log.info("No data for state {}. Fetching page 1 from API...", stateCode);
            fetchAndSavePage(stateCode, 1);
        } else {
            log.info("State {} found in DB. Fetching next page asynchronously...", stateCode);
            fetchNextPageAsync(stateCode);
        }

        List<Politician> politicians = party != null && !party.isBlank()
                ? politicianRepository.findAllByStateCodeAndParty(stateCode, party)
                : politicianRepository.findAllByRoles_StateCode(stateCode);

        return politicians.stream().map(politicianMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public void syncFromApi() {
        log.info("Starting full sync from OpenStates API...");
        List<OpenStatesPersonResponse> responses = openStatesApiService.fetchAllPoliticians();
        List<Politician> politicians = responses.stream()
                .map(politicianMapper::toEntity)
                .toList();
        politicianRepository.saveAll(politicians);
        log.info("Full sync completed. {} politicians saved.", politicians.size());
    }

    @Async
    @Transactional
    public void fetchNextPageAsync(String stateCode) {
        try {
            int nextPage = stateSyncRepository.findById(stateCode)
                    .map(s -> s.getLastPageFetched() + 1)
                    .orElse(2);

            log.info("Async fetching page {} for state {}...", nextPage, stateCode);
            fetchAndSavePage(stateCode, nextPage);
        } catch (Exception e) {
            log.warn("Async page fetch failed for state {}: {}", stateCode, e.getMessage());
        }
    }

    private void fetchAndSavePage(String stateCode, int page) {
        List<OpenStatesPersonResponse> responses = openStatesApiService.fetchPageForState(stateCode, page);
        if (responses.isEmpty()) {
            log.info("No results for state {} page {}", stateCode, page);
            return;
        }

        List<Politician> politicians = responses.stream()
                .map(politicianMapper::toEntity)
                .toList();

        politicianRepository.saveAll(politicians);

        stateSyncRepository.save(StateSync.builder()
                .stateCode(stateCode)
                .lastPageFetched(page)
                .lastSyncedAt(LocalDateTime.now())
                .build());

        log.info("Saved {} politicians for state {} page {}", politicians.size(), stateCode, page);
    }
}