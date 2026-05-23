package com.openstates.app.service;

import com.openstates.app.dto.PoliticianPageDTO;

public interface PoliticianService {

    PoliticianPageDTO findAll(String stateCode, String party, int page, int size);

    void syncAllFromApi();

    void syncNextPageForState(String stateCode);
}
