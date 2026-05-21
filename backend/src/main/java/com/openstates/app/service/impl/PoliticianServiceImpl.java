package com.openstates.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openstates.app.dto.FilterOptionsDTO;
import com.openstates.app.dto.PoliticianDTO;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.repository.PoliticianRepository;
import com.openstates.app.repository.PoliticianRoleRepository;
import com.openstates.app.service.OpenStatesApiService;
import com.openstates.app.service.PoliticianMapper;
import com.openstates.app.service.PoliticianService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PoliticianServiceImpl implements PoliticianService {

    private final PoliticianRepository politicianRepository;
    private final PoliticianRoleRepository politicianRoleRepository;
    private final OpenStatesApiService openStatesApiService;
    private final PoliticianMapper politicianMapper;

    public PoliticianServiceImpl(
            PoliticianRepository politicianRepository,
            PoliticianRoleRepository politicianRoleRepository,
            OpenStatesApiService openStatesApiService,
            PoliticianMapper politicianMapper) {
        this.politicianRepository = politicianRepository;
        this.politicianRoleRepository = politicianRoleRepository;
        this.openStatesApiService = openStatesApiService;
        this.politicianMapper = politicianMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoliticianDTO> findAll(String state, String party) {
        List<Politician> politicians;

        if (state != null && party != null) {
            politicians = politicianRepository.findAllByStateAndParty(state, party);
        } else if (state != null) {
            politicians = politicianRepository.findAllByRoles_JurisdictionName(state);
        } else if (party != null) {
            politicians = politicianRepository.findAllByParty(party);
        } else {
            politicians = politicianRepository.findAll();
        }

        return politicians.stream().map(politicianMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FilterOptionsDTO getFilterOptions() {
        List<String> states = politicianRoleRepository.findDistinctJurisdictionNames();
        List<String> parties = politicianRepository.findDistinctParties();
        return new FilterOptionsDTO(states, parties);
    }

    @Override
    @Transactional
    public void syncFromApi() {
        log.info("Starting sync from OpenStates API...");

        try {
            List<OpenStatesPersonResponse> responses = openStatesApiService.fetchAllPoliticians();
            List<Politician> politicians = responses.stream()
                    .map(politicianMapper::toEntity)
                    .toList();

            politicianRepository.saveAll(politicians);
            log.info("Sync completed. {} politicians saved.", politicians.size());
        } catch (Exception e) {
            log.error("Sync failed: {}", e.getMessage(), e);
        }
    }
}
