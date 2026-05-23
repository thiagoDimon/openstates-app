package com.openstates.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openstates.app.entity.StateSync;

@Repository
public interface StateSyncRepository extends JpaRepository<StateSync, String> {
}
