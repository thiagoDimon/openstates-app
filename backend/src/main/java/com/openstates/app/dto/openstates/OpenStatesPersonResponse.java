package com.openstates.app.dto.openstates;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenStatesPersonResponse(
        String id,
        String name,
        @JsonProperty("given_name") String givenName,
        @JsonProperty("family_name") String familyName,
        String party,
        String image,
        String email,
        String gender,
        @JsonProperty("birth_date") String birthDate,
        @JsonProperty("openstates_url") String openstatesUrl,
        @JsonProperty("current_role") OpenStatesCurrentRole currentRole,
        OpenStatesJurisdiction jurisdiction,
        Map<String, Object> extras
) {}
