package com.medapp.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID id,
        UUID pharmacyId,
        UUID medicineId,
        String medicineName,
        int quantity,
        BigDecimal unitPrice,
        UUID prescriptionId,
        boolean prescriptionRequired
) {
}
