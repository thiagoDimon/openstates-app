package com.openstates.app.service;

import java.util.List;

import com.openstates.app.dto.openstates.OpenStatesPersonResponse;

public interface OpenStatesApiService {

    List<OpenStatesPersonResponse> fetchAllPoliticians();
    List<OpenStatesPersonResponse> fetchPageForState(String stateCode, int page);
}
