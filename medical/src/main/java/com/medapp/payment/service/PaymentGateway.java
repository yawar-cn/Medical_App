package com.medapp.payment.service;

import java.math.BigDecimal;

public interface PaymentGateway {

    GatewayOrder createOrder(String internalOrderId, BigDecimal amount);

    boolean verifyWebhookSignature(String rawPayload, String signatureHeader);

    GatewayRefund refund(String providerPaymentId, BigDecimal amount, String reason);

    String publicKey();

    record GatewayOrder(String providerOrderId) {}

    record GatewayRefund(String refundId, boolean success) {}
}
