package com.medapp.inventory.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.inventory.dto.InventoryDto;
import com.medapp.inventory.dto.InventoryUpsertRequest;
import com.medapp.inventory.dto.StockValidationResponse;
import com.medapp.inventory.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{pharmacyId}")
    @PreAuthorize("hasAnyRole('PHARMACY','ADMIN')")
    public ApiResponse<InventoryDto> create(@AuthenticationPrincipal UUID actorId,
                                            @PathVariable UUID pharmacyId,
                                            @RequestBody @Valid InventoryUpsertRequest request) {
        return ApiResponse.ok("Inventory item created", inventoryService.create(actorId, pharmacyId, request));
    }

    @PutMapping("/{pharmacyId}")
    @PreAuthorize("hasAnyRole('PHARMACY','ADMIN')")
    public ApiResponse<InventoryDto> upsert(@AuthenticationPrincipal UUID actorId,
                                            @PathVariable UUID pharmacyId,
                                            @RequestBody @Valid InventoryUpsertRequest request) {
        return ApiResponse.ok("Inventory item upserted", inventoryService.upsert(actorId, pharmacyId, request));
    }

    @GetMapping("/{pharmacyId}")
    @PreAuthorize("hasAnyRole('PHARMACY','ADMIN')")
    public ApiResponse<List<InventoryDto>> list(@PathVariable UUID pharmacyId) {
        return ApiResponse.ok("Inventory fetched", inventoryService.list(pharmacyId));
    }

    @GetMapping("/validate")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','ADMIN')")
    public ApiResponse<StockValidationResponse> validate(@RequestParam UUID pharmacyId,
                                                         @RequestParam UUID medicineId,
                                                         @RequestParam int quantity) {
        return ApiResponse.ok("Stock validation complete", inventoryService.validateStock(pharmacyId, medicineId, quantity));
    }
}
