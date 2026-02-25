package com.medapp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
public abstract class SoftDeleteEntity extends BaseEntity {

    @Column
    private Instant deletedAt;

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
