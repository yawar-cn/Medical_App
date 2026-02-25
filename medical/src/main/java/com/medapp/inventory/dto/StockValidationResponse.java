package com.medapp.inventory.dto;

import java.util.UUID;

public record StockValidationResponse(
        UUID pharmacyId,
        UUID medicineId,
        boolean available,
        String reason
) {
}
