package com.openstates.app.service;

import com.openstates.app.dto.PoliticianDTO;
import com.openstates.app.dto.PoliticianRoleDTO;
import com.openstates.app.dto.openstates.OpenStatesCurrentRole;
import com.openstates.app.dto.openstates.OpenStatesJurisdiction;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.entity.PoliticianRole;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PoliticianMapper {

    public PoliticianDTO toDTO(Politician politician) {
        List<PoliticianRoleDTO> roles = politician.getRoles() == null
                ? Collections.emptyList()
                : politician.getRoles().stream().map(this::toRoleDTO).toList();

        return new PoliticianDTO(
                politician.getId(),
                politician.getName(),
                politician.getGivenName(),
                politician.getFamilyName(),
                politician.getParty(),
                politician.getImageUrl(),
                politician.getEmail(),
                politician.getGender(),
                politician.getBirthDate(),
                politician.getOpenstatesUrl(),
                roles
        );
    }

    public Politician toEntity(OpenStatesPersonResponse response) {
        Politician politician = Politician.builder()
                .id(response.id())
                .name(response.name())
                .givenName(response.givenName())
                .familyName(response.familyName())
                .party(response.party())
                .imageUrl(response.image())
                .email(response.email())
                .gender(response.gender())
                .birthDate(response.birthDate())
                .openstatesUrl(response.openstatesUrl())
                .extraData(response.extras())
                .build();

        OpenStatesCurrentRole currentRole = response.currentRole();
        OpenStatesJurisdiction jurisdiction = response.jurisdiction();

        if (currentRole != null && jurisdiction != null) {
            PoliticianRole role = PoliticianRole.builder()
                    .politician(politician)
                    .title(currentRole.title())
                    .orgClassification(currentRole.orgClassification())
                    .district(currentRole.district())
                    .jurisdictionName(jurisdiction.name())
                    .jurisdictionId(jurisdiction.id())
                    .build();

            politician.getRoles().add(role);
        }

        return politician;
    }

    private PoliticianRoleDTO toRoleDTO(PoliticianRole role) {
        return new PoliticianRoleDTO(
                role.getId(),
                role.getTitle(),
                role.getOrgClassification(),
                role.getDistrict(),
                role.getJurisdictionName(),
                role.getJurisdictionId()
        );
    }
}
