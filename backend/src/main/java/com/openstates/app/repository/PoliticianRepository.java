package com.openstates.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openstates.app.entity.Politician;

@Repository
public interface PoliticianRepository extends JpaRepository<Politician, String> {

    @Query("SELECT COUNT(DISTINCT p.id) FROM Politician p JOIN p.roles r WHERE r.stateCode = :stateCode")
    long countByStateCode(@Param("stateCode") String stateCode);

    @Query(value = "SELECT DISTINCT p FROM Politician p JOIN p.roles r WHERE r.stateCode = :stateCode",
           countQuery = "SELECT COUNT(DISTINCT p.id) FROM Politician p JOIN p.roles r WHERE r.stateCode = :stateCode")
    Page<Politician> findPageByStateCode(@Param("stateCode") String stateCode, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Politician p JOIN p.roles r WHERE r.stateCode = :stateCode AND p.party = :party",
           countQuery = "SELECT COUNT(DISTINCT p.id) FROM Politician p JOIN p.roles r WHERE r.stateCode = :stateCode AND p.party = :party")
    Page<Politician> findPageByStateCodeAndParty(@Param("stateCode") String stateCode, @Param("party") String party, Pageable pageable);
}
