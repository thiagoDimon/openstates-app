package com.openstates.app.service;

import com.openstates.app.dto.openstates.OpenStatesPersonResponse;

import java.util.List;

public interface OpenStatesApiService {

    List<OpenStatesPersonResponse> fetchAllPoliticians();
}
