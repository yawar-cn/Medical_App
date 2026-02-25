package com.medapp.audit.controller;

import com.medapp.audit.dto.AuditLogDto;
import com.medapp.audit.service.AuditService;
import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<AuditLogDto>> list(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Audit logs fetched", auditService.list(page, size));
    }

    @GetMapping("/by-actor")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<AuditLogDto>> byActor(@RequestParam UUID actorId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Audit logs fetched", auditService.listByActor(actorId, page, size));
    }
}
