package com.medapp.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentInitiateResponse(
        UUID paymentId,
        UUID orderId,
        String providerOrderId,
        BigDecimal amount,
        String publicKey
) {
}
