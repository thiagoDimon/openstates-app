package com.openstates.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openstates.app.entity.Politician;

@Repository
public interface PoliticianRepository extends JpaRepository<Politician, String> {

    List<Politician> findAllByParty(String party);

    List<Politician> findAllByRoles_JurisdictionName(String jurisdictionName);

    @Query("SELECT DISTINCT p FROM Politician p JOIN p.roles r WHERE r.jurisdictionName = :state AND p.party = :party")
    List<Politician> findAllByStateAndParty(@Param("state") String state, @Param("party") String party);

    @Query("SELECT DISTINCT p.party FROM Politician p WHERE p.party IS NOT NULL ORDER BY p.party")
    List<String> findDistinctParties();
}
