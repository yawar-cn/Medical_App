package com.medapp.inventory.dto;

import com.medapp.inventory.validation.ValidExpiryDate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InventoryUpsertRequest(
        @NotNull UUID medicineId,
        @NotBlank String batchNumber,
        @NotNull @ValidExpiryDate LocalDate expiryDate,
        @Min(0) int quantity,
        @NotNull BigDecimal sellingPrice
) {
}
