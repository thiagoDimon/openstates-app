package com.openstates.app.dto;

import java.util.UUID;

public record PoliticianRoleDTO(
        UUID id,
        String title,
        String orgClassification,
        String district,
        String jurisdictionName,
        String jurisdictionId
) {}
