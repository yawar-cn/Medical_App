package com.medapp.audit.domain;

import com.medapp.audit.entity.AuditLog;
import com.medapp.audit.repository.AuditLogRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AuditDomain {

    private final AuditLogRepository auditLogRepository;

    public AuditDomain(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog create(UUID actorUserId, String action, String entityType, UUID entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorUserId(actorUserId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setActionTimestamp(Instant.now());
        return auditLogRepository.save(auditLog);
    }

    public Page<AuditLog> getAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getByActor(UUID actorId, Pageable pageable) {
        return auditLogRepository.findByActorUserId(actorId, pageable);
    }
}
