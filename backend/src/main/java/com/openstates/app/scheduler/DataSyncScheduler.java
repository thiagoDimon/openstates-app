package com.openstates.app.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.openstates.app.service.PoliticianService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncScheduler {

    @NonNull private final PoliticianService politicianService;

    @Scheduled(cron = "${openstates.sync.cron}")
    public void scheduledSync() {
        log.info("Running scheduled sync...");
        try {
            politicianService.syncAllFromApi();
        } catch (Exception e) {
            log.error("Scheduled sync failed unexpectedly: {}", e.getMessage(), e);
        }
    }
}
