package com.medapp.delivery.dto;

import com.medapp.delivery.entity.DeliveryStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeliveryAssignmentDto(
        UUID id,
        UUID orderId,
        UUID riderId,
        DeliveryStatus status,
        BigDecimal earningAmount,
        Instant assignedAt,
        Instant deliveredAt
) {
}
