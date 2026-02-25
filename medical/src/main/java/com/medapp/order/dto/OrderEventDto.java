package com.medapp.order.dto;

import com.medapp.order.entity.OrderStatus;
import java.time.Instant;
import java.util.UUID;

public record OrderEventDto(
        UUID id,
        OrderStatus fromStatus,
        OrderStatus toStatus,
        UUID actorUserId,
        String source,
        Instant eventTime,
        String remarks
) {
}
