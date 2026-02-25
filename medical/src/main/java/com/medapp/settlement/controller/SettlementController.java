package com.medapp.settlement.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.settlement.dto.SettlementDto;
import com.medapp.settlement.dto.SettlementMarkPaidRequest;
import com.medapp.settlement.service.SettlementService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/{orderId}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SettlementDto> markPaid(@AuthenticationPrincipal UUID actorUserId,
                                               @PathVariable UUID orderId,
                                               @RequestBody @Valid SettlementMarkPaidRequest request) {
        return ApiResponse.ok("Settlement marked paid", settlementService.markSettled(actorUserId, orderId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<SettlementDto>> all(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Settlements fetched", settlementService.all(page, size));
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACY')")
    public ApiResponse<PageResponse<SettlementDto>> byPharmacy(@PathVariable UUID pharmacyId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Pharmacy settlements fetched", settlementService.byPharmacy(pharmacyId, page, size));
    }

    @GetMapping("/rider/{riderId}")
    @PreAuthorize("hasAnyRole('ADMIN','RIDER')")
    public ApiResponse<PageResponse<SettlementDto>> byRider(@PathVariable UUID riderId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Rider settlements fetched", settlementService.byRider(riderId, page, size));
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<SettlementDto>> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok("Settlement report fetched", settlementService.byDateRange(from, to, page, size));
    }
}
