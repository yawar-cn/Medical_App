package com.medapp.pharmacy.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.pharmacy.dto.PharmacyApprovalRequest;
import com.medapp.pharmacy.dto.PharmacyCreateRequest;
import com.medapp.pharmacy.dto.PharmacyDto;
import com.medapp.pharmacy.dto.PharmacyUpdateRequest;
import com.medapp.pharmacy.service.PharmacyService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pharmacies")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PHARMACY')")
    public ApiResponse<PharmacyDto> create(@AuthenticationPrincipal UUID userId,
                                           @RequestBody @Valid PharmacyCreateRequest request) {
        return ApiResponse.ok("Pharmacy created", pharmacyService.create(userId, request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PHARMACY')")
    public ApiResponse<PharmacyDto> me(@AuthenticationPrincipal UUID userId) {
        return ApiResponse.ok("Pharmacy profile fetched", pharmacyService.me(userId));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('PHARMACY')")
    public ApiResponse<PharmacyDto> update(@AuthenticationPrincipal UUID userId,
                                           @RequestBody @Valid PharmacyUpdateRequest request) {
        return ApiResponse.ok("Pharmacy updated", pharmacyService.update(userId, request));
    }

    @PostMapping("/admin/{pharmacyId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PharmacyDto> review(@AuthenticationPrincipal UUID adminId,
                                           @PathVariable UUID pharmacyId,
                                           @RequestBody @Valid PharmacyApprovalRequest request) {
        return ApiResponse.ok("Pharmacy review completed", pharmacyService.review(adminId, pharmacyId, request));
    }

    @GetMapping("/approved")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','ADMIN','RIDER')")
    public ApiResponse<PageResponse<PharmacyDto>> approved(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Approved pharmacies fetched", pharmacyService.approved(page, size));
    }
}
