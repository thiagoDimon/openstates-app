package com.openstates.app.scheduler;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.service.PoliticianService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataSyncScheduler {

    private final PoliticianService politicianService;
    private final PoliticianRepository politicianRepository;

    public DataSyncScheduler(PoliticianService politicianService, PoliticianRepository politicianRepository) {
        this.politicianService = politicianService;
        this.politicianRepository = politicianRepository;
    }

    @Scheduled(cron = "${openstates.sync.cron}")
    public void scheduledSync() {
        log.info("Running scheduled sync...");
        politicianService.syncFromApi();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartupIfEmpty() {
        if (politicianRepository.count() == 0) {
            log.info("Database is empty. Running initial sync on startup...");
            politicianService.syncFromApi();
        }
    }
}
