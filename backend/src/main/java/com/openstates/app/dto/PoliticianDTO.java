package com.openstates.app.dto;

import java.util.List;

public record PoliticianDTO(
        String id,
        String name,
        String givenName,
        String familyName,
        String party,
        String imageUrl,
        String email,
        String gender,
        String birthDate,
        String openstatesUrl,
        List<PoliticianRoleDTO> roles
) {}
