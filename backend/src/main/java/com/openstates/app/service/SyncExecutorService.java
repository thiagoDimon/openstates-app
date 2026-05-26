package com.openstates.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openstates.app.dto.openstates.OpenStatesApiResponse;
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

    @NonNull
    private final PoliticianRepository politicianRepository;
    @NonNull
    private final StateSyncRepository stateSyncRepository;
    @NonNull
    private final OpenStatesApiService openStatesApiService;
    @NonNull
    private final PoliticianMapper politicianMapper;

    @Transactional
    public void fetchAndSavePage(String stateCode, int page) {
        OpenStatesApiResponse response = openStatesApiService.fetchPageForState(stateCode, page);
        List<OpenStatesPersonResponse> results = response.results() != null ? response.results() : List.of();

        int maxPage = response.pagination() != null ? response.pagination().maxPage() : page;

        if (results.isEmpty()) {
            log.info("No results for state {} page {}", stateCode, page);
            stateSyncRepository.findById(stateCode).ifPresent(existing -> {
                existing.setMaxPage(existing.getLastPageFetched());
                stateSyncRepository.save(existing);
            });
            return;
        }

        List<Politician> politicians = results.stream()
                .map(politicianMapper::toEntity)
                .toList();
        politicianRepository.saveAll(politicians);

        stateSyncRepository.save(StateSync.builder()
                .stateCode(stateCode)
                .lastPageFetched(page)
                .lastSyncedAt(LocalDateTime.now())
                .maxPage(maxPage)
                .build());

        log.info("Saved {} politicians for state {} page {} of {}", politicians.size(), stateCode, page, maxPage);
    }

    @Transactional
    public void syncNextPage(String stateCode) {
        int nextPage = stateSyncRepository.findById(stateCode)
                .map(s -> s.getLastPageFetched() + 1)
                .orElse(1);
        log.info("Fetching page {} for state {}...", nextPage, stateCode);
        fetchAndSavePage(stateCode, nextPage);
    }

    public boolean hasMoreApiPages(String stateCode) {
        return stateSyncRepository.findById(stateCode)
                .map(s -> s.getMaxPage() == 0 || s.getLastPageFetched() < s.getMaxPage())
                .orElse(true);
    }
}
