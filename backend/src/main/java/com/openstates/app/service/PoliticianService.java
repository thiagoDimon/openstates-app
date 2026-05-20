package com.openstates.app.service;

import com.openstates.app.dto.FilterOptionsDTO;
import com.openstates.app.dto.PoliticianDTO;

import java.util.List;

public interface PoliticianService {

    List<PoliticianDTO> findAll(String state, String party);

    FilterOptionsDTO getFilterOptions();

    void syncFromApi();
}
