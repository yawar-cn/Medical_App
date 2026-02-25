package com.medapp.audit.service;

import com.medapp.audit.domain.AuditDomain;
import com.medapp.audit.dto.AuditLogDto;
import com.medapp.audit.mapper.AuditMapper;
import com.medapp.common.dto.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditDomain auditDomain;

    public AuditService(AuditDomain auditDomain) {
        this.auditDomain = auditDomain;
    }

    public void record(UUID actorUserId, String action, String entityType, UUID entityId, String details) {
        auditDomain.create(actorUserId, action, entityType, entityId, details);
    }

    public PageResponse<AuditLogDto> list(int page, int size) {
        Page<AuditLogDto> logs = auditDomain.getAll(PageRequest.of(page, size)).map(AuditMapper::toDto);
        return new PageResponse<>(logs.getContent(), logs.getNumber(), logs.getSize(), logs.getTotalElements(), logs.getTotalPages(), logs.isLast());
    }

    public PageResponse<AuditLogDto> listByActor(UUID actorId, int page, int size) {
        Page<AuditLogDto> logs = auditDomain.getByActor(actorId, PageRequest.of(page, size)).map(AuditMapper::toDto);
        return new PageResponse<>(logs.getContent(), logs.getNumber(), logs.getSize(), logs.getTotalElements(), logs.getTotalPages(), logs.isLast());
    }
}
