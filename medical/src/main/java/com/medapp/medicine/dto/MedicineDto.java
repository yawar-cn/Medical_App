package com.medapp.medicine.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MedicineDto(
        UUID id,
        String name,
        String genericName,
        String manufacturer,
        String category,
        BigDecimal gstPercentage,
        boolean prescriptionRequired,
        BigDecimal mrp,
        boolean active
) {
}
