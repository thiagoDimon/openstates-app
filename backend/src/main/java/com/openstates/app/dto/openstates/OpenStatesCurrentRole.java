package com.openstates.app.dto.openstates;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenStatesCurrentRole(
        String title,
        @JsonProperty("org_classification") String orgClassification,
        String district,
        @JsonProperty("division_id") String divisionId
) {}
