package com.medapp.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditLogDto(
        UUID id,
        UUID actorUserId,
        Instant actionTimestamp,
        String action,
        String entityType,
        UUID entityId,
        String details
) {
}
