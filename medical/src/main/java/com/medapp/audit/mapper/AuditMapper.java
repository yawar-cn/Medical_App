package com.medapp.audit.mapper;

import com.medapp.audit.dto.AuditLogDto;
import com.medapp.audit.entity.AuditLog;

public final class AuditMapper {

    private AuditMapper() {
    }

    public static AuditLogDto toDto(AuditLog auditLog) {
        return new AuditLogDto(
                auditLog.getId(),
                auditLog.getActorUserId(),
                auditLog.getActionTimestamp(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDetails()
        );
    }
}
