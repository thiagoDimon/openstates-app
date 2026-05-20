package com.openstates.app.service;

import java.util.List;

import com.openstates.app.dto.FilterOptionsDTO;
import com.openstates.app.dto.PoliticianDTO;

public interface PoliticianService {

    List<PoliticianDTO> findAll(String state, String party);

    FilterOptionsDTO getFilterOptions();

    void syncFromApi();
}
