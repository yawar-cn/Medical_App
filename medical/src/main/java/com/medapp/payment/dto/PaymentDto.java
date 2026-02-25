package com.medapp.payment.dto;

import com.medapp.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentDto(
        UUID id,
        UUID orderId,
        String providerOrderId,
        String providerPaymentId,
        BigDecimal amount,
        PaymentStatus status,
        Instant completedAt,
        Instant refundedAt
) {
}
