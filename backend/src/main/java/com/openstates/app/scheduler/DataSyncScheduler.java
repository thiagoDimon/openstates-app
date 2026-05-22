package com.openstates.app.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.openstates.app.service.PoliticianService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataSyncScheduler {

    private final PoliticianService politicianService;

    public DataSyncScheduler(PoliticianService politicianService) {
        this.politicianService = politicianService;
    }

    @Scheduled(cron = "${openstates.sync.cron}")
    public void scheduledSync() {
        log.info("Running scheduled sync...");
        try {
            politicianService.syncFromApi();
        } catch (Exception e) {
            log.error("Scheduled sync failed unexpectedly: {}", e.getMessage(), e);
        }
    }
}
