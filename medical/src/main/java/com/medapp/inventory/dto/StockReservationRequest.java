package com.medapp.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StockReservationRequest(
        @NotNull UUID medicineId,
        @Min(1) int quantity
) {
}
