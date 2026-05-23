package com.openstates.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.entity.StateSync;
import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.repository.StateSyncRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncExecutorService {

    @NonNull private final PoliticianRepository politicianRepository;
    @NonNull private final StateSyncRepository stateSyncRepository;
    @NonNull private final OpenStatesApiService openStatesApiService;
    @NonNull private final PoliticianMapper politicianMapper;

    @Transactional
    public void fetchAndSavePage(String stateCode, int page) {
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

    @Transactional
    public void syncNextPage(String stateCode) {
        int nextPage = stateSyncRepository.findById(stateCode)
                .map(s -> s.getLastPageFetched() + 1)
                .orElse(1);
        log.info("Manual sync: fetching page {} for state {}...", nextPage, stateCode);
        fetchAndSavePage(stateCode, nextPage);
    }
}
