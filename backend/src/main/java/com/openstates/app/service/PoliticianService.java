package com.openstates.app.service;

import java.util.List;

import com.openstates.app.dto.PoliticianDTO;

public interface PoliticianService {

    List<PoliticianDTO> findAll(String state, String party);

    void syncFromApi();
}
