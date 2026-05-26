package com.openstates.app.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openstates.app.dto.PoliticianDTO;
import com.openstates.app.dto.openstates.OpenStatesCurrentRole;
import com.openstates.app.dto.openstates.OpenStatesJurisdiction;
import com.openstates.app.dto.openstates.OpenStatesPersonResponse;
import com.openstates.app.entity.Politician;
import com.openstates.app.entity.PoliticianRole;

class PoliticianMapperTest {

    private PoliticianMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PoliticianMapper();
    }

    @Test
    void toDTO_withAllFields_mapsAllFieldsCorrectly() {
        PoliticianRole role = PoliticianRole.builder()
                .id(UUID.randomUUID())
                .title("Senator")
                .stateCode("ca")
                .build();

        Politician politician = Politician.builder()
                .id("ocd-person/123")
                .name("Jane Doe")
                .givenName("Jane")
                .familyName("Doe")
                .party("Democratic")
                .imageUrl("https://example.com/photo.jpg")
                .email("jane@example.com")
                .gender("F")
                .birthDate("1975-04-01")
                .openstatesUrl("https://openstates.org/person/123")
                .roles(List.of(role))
                .build();

        PoliticianDTO dto = mapper.toDTO(politician);

        assertThat(dto.id()).isEqualTo("ocd-person/123");
        assertThat(dto.name()).isEqualTo("Jane Doe");
        assertThat(dto.givenName()).isEqualTo("Jane");
        assertThat(dto.familyName()).isEqualTo("Doe");
        assertThat(dto.party()).isEqualTo("Democratic");
        assertThat(dto.imageUrl()).isEqualTo("https://example.com/photo.jpg");
        assertThat(dto.email()).isEqualTo("jane@example.com");
        assertThat(dto.gender()).isEqualTo("F");
        assertThat(dto.birthDate()).isEqualTo("1975-04-01");
        assertThat(dto.openstatesUrl()).isEqualTo("https://openstates.org/person/123");
        assertThat(dto.roles()).hasSize(1);
    }

    @Test
    void toDTO_withNullRoles_returnsEmptyList() {
        Politician politician = Politician.builder()
                .id("ocd-person/123")
                .name("Jane Doe")
                .build();
        politician.setRoles(null);

        PoliticianDTO dto = mapper.toDTO(politician);

        assertThat(dto.roles()).isEmpty();
    }

    @Test
    void toEntity_withAllFields_mapsAllFieldsCorrectly() {
        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/456", "John Smith", "John", "Smith", "Republican",
                "https://example.com/photo.jpg", "john@example.com", "M",
                "1960-07-04", "https://openstates.org/person/456",
                null, null, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getId()).isEqualTo("ocd-person/456");
        assertThat(politician.getName()).isEqualTo("John Smith");
        assertThat(politician.getGivenName()).isEqualTo("John");
        assertThat(politician.getFamilyName()).isEqualTo("Smith");
        assertThat(politician.getParty()).isEqualTo("Republican");
        assertThat(politician.getImageUrl()).isEqualTo("https://example.com/photo.jpg");
        assertThat(politician.getEmail()).isEqualTo("john@example.com");
        assertThat(politician.getGender()).isEqualTo("M");
        assertThat(politician.getBirthDate()).isEqualTo("1960-07-04");
        assertThat(politician.getOpenstatesUrl()).isEqualTo("https://openstates.org/person/456");
    }

    @Test
    void toEntity_withCurrentRoleAndJurisdiction_addsOneRole() {
        OpenStatesCurrentRole currentRole = new OpenStatesCurrentRole("Senator", "upper", "1", null);
        OpenStatesJurisdiction jurisdiction = new OpenStatesJurisdiction(
                "ocd-jurisdiction/country:us/state:ca/government", "California", "government"
        );

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/456", "John Smith", null, null, null,
                null, null, null, null, null, currentRole, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles()).hasSize(1);
        PoliticianRole role = politician.getRoles().get(0);
        assertThat(role.getTitle()).isEqualTo("Senator");
        assertThat(role.getJurisdictionName()).isEqualTo("California");
        assertThat(role.getStateCode()).isEqualTo("ca");
    }

    @Test
    void toEntity_withDistrictJurisdictionId_extractsCorrectStateCode() {
        OpenStatesCurrentRole currentRole = new OpenStatesCurrentRole("Delegate", "upper", "at-large", null);
        OpenStatesJurisdiction jurisdiction = new OpenStatesJurisdiction(
                "ocd-jurisdiction/country:us/district:dc/government", "District of Columbia", "government"
        );

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/2", "Test", null, null, null,
                null, null, null, null, null, currentRole, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles().get(0).getStateCode()).isEqualTo("dc");
    }
}
