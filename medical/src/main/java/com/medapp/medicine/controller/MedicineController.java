package com.medapp.medicine.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.medicine.dto.MedicineCreateRequest;
import com.medapp.medicine.dto.MedicineDto;
import com.medapp.medicine.dto.MedicineUpdateRequest;
import com.medapp.medicine.service.MedicineService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MedicineDto> create(@AuthenticationPrincipal UUID adminId,
                                           @RequestBody @Valid MedicineCreateRequest request) {
        return ApiResponse.ok("Medicine created", medicineService.create(adminId, request));
    }

    @PutMapping("/{medicineId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MedicineDto> update(@AuthenticationPrincipal UUID adminId,
                                           @PathVariable UUID medicineId,
                                           @RequestBody @Valid MedicineUpdateRequest request) {
        return ApiResponse.ok("Medicine updated", medicineService.update(adminId, medicineId, request));
    }

    @GetMapping("/{medicineId}")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','ADMIN','RIDER')")
    public ApiResponse<MedicineDto> get(@PathVariable UUID medicineId) {
        return ApiResponse.ok("Medicine fetched", medicineService.get(medicineId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','PHARMACY','ADMIN','RIDER')")
    public ApiResponse<PageResponse<MedicineDto>> search(@RequestParam(required = false) String query,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Medicines fetched", medicineService.search(query, page, size));
    }
}
