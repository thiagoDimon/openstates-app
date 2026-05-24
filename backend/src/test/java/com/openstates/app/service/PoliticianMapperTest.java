package com.openstates.app.service;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openstates.app.dto.PoliticianDTO;
import com.openstates.app.dto.PoliticianRoleDTO;
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

    // --- toDTO ---

    @Test
    void toDTO_withAllFields_mapsAllFieldsCorrectly() {
        PoliticianRole role = PoliticianRole.builder()
                .id(UUID.randomUUID())
                .title("Senator")
                .orgClassification("upper")
                .district("1")
                .jurisdictionName("California")
                .jurisdictionId("ocd-jurisdiction/country:us/state:ca/government")
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
    void toDTO_withMultipleRoles_mapsAllRoles() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        PoliticianRole role1 = PoliticianRole.builder()
                .id(id1)
                .title("Senator")
                .orgClassification("upper")
                .district("1")
                .jurisdictionName("California")
                .jurisdictionId("ocd-jurisdiction/country:us/state:ca/government")
                .build();

        PoliticianRole role2 = PoliticianRole.builder()
                .id(id2)
                .title("Representative")
                .orgClassification("lower")
                .district("5")
                .jurisdictionName("Texas")
                .jurisdictionId("ocd-jurisdiction/country:us/state:tx/government")
                .build();

        Politician politician = Politician.builder()
                .id("ocd-person/123")
                .name("Jane Doe")
                .roles(List.of(role1, role2))
                .build();

        PoliticianDTO dto = mapper.toDTO(politician);

        assertThat(dto.roles()).hasSize(2);

        PoliticianRoleDTO dtoRole1 = dto.roles().get(0);
        assertThat(dtoRole1.id()).isEqualTo(id1);
        assertThat(dtoRole1.title()).isEqualTo("Senator");
        assertThat(dtoRole1.orgClassification()).isEqualTo("upper");
        assertThat(dtoRole1.district()).isEqualTo("1");
        assertThat(dtoRole1.jurisdictionName()).isEqualTo("California");
        assertThat(dtoRole1.jurisdictionId()).isEqualTo("ocd-jurisdiction/country:us/state:ca/government");

        PoliticianRoleDTO dtoRole2 = dto.roles().get(1);
        assertThat(dtoRole2.id()).isEqualTo(id2);
        assertThat(dtoRole2.title()).isEqualTo("Representative");
        assertThat(dtoRole2.orgClassification()).isEqualTo("lower");
        assertThat(dtoRole2.district()).isEqualTo("5");
        assertThat(dtoRole2.jurisdictionName()).isEqualTo("Texas");
        assertThat(dtoRole2.jurisdictionId()).isEqualTo("ocd-jurisdiction/country:us/state:tx/government");
    }

    // --- toEntity ---

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
                "ocd-person/456", "John Smith", "John", "Smith", "Republican",
                null, null, null, null, null, currentRole, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles()).hasSize(1);
        PoliticianRole role = politician.getRoles().get(0);
        assertThat(role.getTitle()).isEqualTo("Senator");
        assertThat(role.getOrgClassification()).isEqualTo("upper");
        assertThat(role.getDistrict()).isEqualTo("1");
        assertThat(role.getJurisdictionName()).isEqualTo("California");
        assertThat(role.getJurisdictionId()).isEqualTo("ocd-jurisdiction/country:us/state:ca/government");
        assertThat(role.getStateCode()).isEqualTo("ca");
    }

    @Test
    void toEntity_withNullCurrentRole_hasNoRoles() {
        OpenStatesJurisdiction jurisdiction = new OpenStatesJurisdiction(
                "ocd-jurisdiction/country:us/state:ca/government", "California", "government"
        );

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/456", "John Smith", "John", "Smith", "Republican",
                null, null, null, null, null, null, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles()).isEmpty();
    }

    @Test
    void toEntity_withNullJurisdiction_hasNoRoles() {
        OpenStatesCurrentRole currentRole = new OpenStatesCurrentRole("Senator", "upper", "1", null);

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/456", "John Smith", "John", "Smith", "Republican",
                null, null, null, null, null, currentRole, null, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles()).isEmpty();
    }

    // --- extractStateCode (via toEntity) ---

    @Test
    void toEntity_withStateJurisdictionId_extractsCorrectStateCode() {
        OpenStatesCurrentRole currentRole = new OpenStatesCurrentRole("Senator", "upper", "1", null);
        OpenStatesJurisdiction jurisdiction = new OpenStatesJurisdiction(
                "ocd-jurisdiction/country:us/state:ca/government", "California", "government"
        );

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/1", "Test", null, null, null,
                null, null, null, null, null, currentRole, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles().get(0).getStateCode()).isEqualTo("ca");
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

    @Test
    void toEntity_withNullJurisdictionId_stateCodeIsNull() {
        OpenStatesCurrentRole currentRole = new OpenStatesCurrentRole("Senator", "upper", "1", null);
        OpenStatesJurisdiction jurisdiction = new OpenStatesJurisdiction(null, "Unknown", "government");

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/3", "Test", null, null, null,
                null, null, null, null, null, currentRole, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles().get(0).getStateCode()).isNull();
    }

    @Test
    void toEntity_withUnknownJurisdictionFormat_stateCodeIsNull() {
        OpenStatesCurrentRole currentRole = new OpenStatesCurrentRole("Mayor", "executive", "city", null);
        OpenStatesJurisdiction jurisdiction = new OpenStatesJurisdiction(
                "ocd-jurisdiction/country:us/government", "US Government", "government"
        );

        OpenStatesPersonResponse response = new OpenStatesPersonResponse(
                "ocd-person/4", "Test", null, null, null,
                null, null, null, null, null, currentRole, jurisdiction, null
        );

        Politician politician = mapper.toEntity(response);

        assertThat(politician.getRoles().get(0).getStateCode()).isNull();
    }
}
