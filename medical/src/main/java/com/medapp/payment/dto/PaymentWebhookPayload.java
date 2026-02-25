package com.medapp.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentWebhookPayload(
        String event,
        String providerOrderId,
        String providerPaymentId,
        BigDecimal amount,
        String status,
        String raw
) {
}
