package com.medapp.prescription.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.prescription.dto.PrescriptionDto;
import com.medapp.prescription.dto.PrescriptionReviewRequest;
import com.medapp.prescription.dto.PrescriptionUploadRequest;
import com.medapp.prescription.service.PrescriptionService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PrescriptionDto> upload(@AuthenticationPrincipal UUID userId,
                                               @RequestBody @Valid PrescriptionUploadRequest request) {
        return ApiResponse.ok("Prescription uploaded", prescriptionService.upload(userId, request));
    }

    @PostMapping("/{prescriptionId}/review")
    @PreAuthorize("hasAnyRole('PHARMACY','ADMIN')")
    public ApiResponse<PrescriptionDto> review(@AuthenticationPrincipal UUID reviewerId,
                                               @PathVariable UUID prescriptionId,
                                               @RequestBody @Valid PrescriptionReviewRequest request) {
        return ApiResponse.ok("Prescription reviewed", prescriptionService.review(reviewerId, prescriptionId, request));
    }

    @GetMapping("/{prescriptionId}")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','ADMIN')")
    public ApiResponse<PrescriptionDto> get(@PathVariable UUID prescriptionId) {
        return ApiResponse.ok("Prescription fetched", prescriptionService.get(prescriptionId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PageResponse<PrescriptionDto>> myPrescriptions(@AuthenticationPrincipal UUID userId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Prescription list fetched", prescriptionService.myPrescriptions(userId, page, size));
    }
}
