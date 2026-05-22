package com.openstates.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openstates.app.entity.PoliticianRole;

@Repository
public interface PoliticianRoleRepository extends JpaRepository<PoliticianRole, UUID> {

    List<PoliticianRole> findAllByPoliticianId(String politicianId);

    void deleteAllByPoliticianId(String politicianId);
}
