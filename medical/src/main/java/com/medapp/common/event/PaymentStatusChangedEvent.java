package com.medapp.common.event;

import com.medapp.payment.entity.PaymentStatus;
import java.util.UUID;

public record PaymentStatusChangedEvent(
        UUID orderId,
        UUID paymentId,
        PaymentStatus status,
        UUID userId
) {
}
