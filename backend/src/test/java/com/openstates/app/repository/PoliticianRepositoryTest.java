package com.openstates.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.openstates.app.entity.Politician;
import com.openstates.app.entity.PoliticianRole;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PoliticianRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PoliticianRepository repository;

    private Politician savedPolitician(String id, String name, String party, String stateCode) {
        Politician politician = Politician.builder()
                .id(id)
                .name(name)
                .party(party)
                .build();

        PoliticianRole role = PoliticianRole.builder()
                .politician(politician)
                .title("Senator")
                .stateCode(stateCode)
                .build();

        politician.getRoles().add(role);
        return entityManager.persist(politician);
    }

    @Test
    void countByStateCode_returnsCorrectCount() {
        savedPolitician("ocd-person/1", "Jane Doe", "Democratic", "ca");
        savedPolitician("ocd-person/2", "John Smith", "Republican", "ca");
        savedPolitician("ocd-person/3", "Alice Brown", "Democratic", "tx");
        entityManager.flush();

        assertThat(repository.countByStateCode("ca")).isEqualTo(2);
    }

    @Test
    void findPageByStateCode_returnsOnlyPoliticiansFromState() {
        savedPolitician("ocd-person/1", "Jane Doe", "Democratic", "ca");
        savedPolitician("ocd-person/2", "John Smith", "Republican", "tx");
        entityManager.flush();

        Page<Politician> result = repository.findPageByStateCode("ca", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("ocd-person/1");
    }

    @Test
    void findPageByStateCodeAndParty_filtersStateAndParty() {
        savedPolitician("ocd-person/1", "Jane Doe", "Democratic", "ca");
        savedPolitician("ocd-person/2", "John Smith", "Republican", "ca");
        entityManager.flush();

        Page<Politician> result = repository.findPageByStateCodeAndParty("ca", "Democratic", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getParty()).isEqualTo("Democratic");
    }
}
