package com.medapp.order.dto;

import com.medapp.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        UUID userId,
        UUID pharmacyId,
        UUID prescriptionId,
        OrderStatus status,
        BigDecimal totalAmount,
        UUID riderId,
        Instant createdAt,
        List<OrderItemDto> items,
        List<OrderEventDto> events
) {
}
