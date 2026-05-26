package com.openstates.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "state_sync")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateSync {

    @Id
    @Column(name = "state_code", length = 2, updatable = false, nullable = false)
    private String stateCode;

    @Column(name = "last_page_fetched", nullable = false)
    private int lastPageFetched;

    @Column(name = "last_synced_at", nullable = false)
    private LocalDateTime lastSyncedAt;

    @Column(name = "max_page", nullable = false)
    private int maxPage;
}
