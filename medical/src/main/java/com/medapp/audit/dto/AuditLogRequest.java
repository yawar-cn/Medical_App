package com.medapp.audit.dto;

import com.medapp.audit.validation.ValidAuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AuditLogRequest(
        @NotNull UUID actorUserId,
        @ValidAuditAction String action,
        @NotBlank String entityType,
        @NotNull UUID entityId,
        @NotBlank String details
) {
}
