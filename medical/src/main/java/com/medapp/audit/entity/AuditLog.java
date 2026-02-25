package com.medapp.audit.entity;

import com.medapp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private UUID actorUserId;

    @Column(nullable = false)
    private Instant actionTimestamp;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 100)
    private String entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false, columnDefinition = "text")
    private String details;

    public UUID getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(UUID actorUserId) {
        this.actorUserId = actorUserId;
    }

    public Instant getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(Instant actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
