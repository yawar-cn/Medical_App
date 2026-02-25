package com.medapp.payment.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentGateway implements PaymentGateway {

    private final String keyId;
    private final String webhookSecret;

    public RazorpayPaymentGateway(@Value("${app.payment.razorpay.key-id}") String keyId,
                                  @Value("${app.payment.razorpay.webhook-secret}") String webhookSecret) {
        this.keyId = keyId;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public GatewayOrder createOrder(String internalOrderId, BigDecimal amount) {
        String providerOrderId = "rp_order_" + UUID.randomUUID();
        return new GatewayOrder(providerOrderId);
    }

    @Override
    public boolean verifyWebhookSignature(String rawPayload, String signatureHeader) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));
            String expected = Base64.getEncoder().encodeToString(digest);
            return expected.equals(signatureHeader);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public GatewayRefund refund(String providerPaymentId, BigDecimal amount, String reason) {
        return new GatewayRefund("rp_refund_" + UUID.randomUUID(), true);
    }

    @Override
    public String publicKey() {
        return keyId;
    }
}
