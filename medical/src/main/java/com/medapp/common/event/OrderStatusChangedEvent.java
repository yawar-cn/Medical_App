package com.medapp.common.event;

import com.medapp.order.entity.OrderStatus;
import java.util.UUID;

public record OrderStatusChangedEvent(
        UUID orderId,
        UUID userId,
        OrderStatus from,
        OrderStatus to,
        String source
) {
}
