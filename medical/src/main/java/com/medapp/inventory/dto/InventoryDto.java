package com.medapp.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InventoryDto(
        UUID id,
        UUID pharmacyId,
        UUID medicineId,
        String medicineName,
        String batchNumber,
        LocalDate expiryDate,
        int quantityAvailable,
        int quantityReserved,
        BigDecimal sellingPrice
) {
}
