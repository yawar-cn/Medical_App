package com.medapp.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        UUID medicineId,
        String medicineName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal gstPercentage
) {
}
