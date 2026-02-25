package com.medapp.order.dto;

import com.medapp.order.entity.OrderStatus;
import com.medapp.order.validation.ValidOrderStatusTransition;

public record OrderStatusTransitionRequest(
        @ValidOrderStatusTransition OrderStatus status,
        String remarks
) {
}
