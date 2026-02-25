package com.medapp.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medapp.audit.service.AuditService;
import com.medapp.common.event.PaymentStatusChangedEvent;
import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderStatus;
import com.medapp.order.service.OrderService;
import com.medapp.payment.domain.PaymentDomain;
import com.medapp.payment.dto.PaymentDto;
import com.medapp.payment.dto.PaymentInitiateRequest;
import com.medapp.payment.dto.PaymentInitiateResponse;
import com.medapp.payment.dto.PaymentWebhookPayload;
import com.medapp.payment.dto.RefundRequest;
import com.medapp.payment.entity.PaymentStatus;
import com.medapp.payment.entity.PaymentTransaction;
import com.medapp.payment.exception.PaymentException;
import com.medapp.payment.mapper.PaymentMapper;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentDomain paymentDomain;
    private final OrderService orderService;
    private final PaymentGateway paymentGateway;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentDomain paymentDomain,
                          OrderService orderService,
                          PaymentGateway paymentGateway,
                          AuditService auditService,
                          ApplicationEventPublisher eventPublisher,
                          ObjectMapper objectMapper) {
        this.paymentDomain = paymentDomain;
        this.orderService = orderService;
        this.paymentGateway = paymentGateway;
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentInitiateResponse initiate(UUID userId, PaymentInitiateRequest request) {
        Order order = orderService.getOrderEntity(request.orderId());
        if (!order.getUser().getId().equals(userId)) {
            throw new PaymentException("Unauthorized order payment attempt", HttpStatus.FORBIDDEN);
        }
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new PaymentException("Order is not eligible for payment", HttpStatus.BAD_REQUEST);
        }

        PaymentGateway.GatewayOrder gatewayOrder = paymentGateway.createOrder(order.getId().toString(), order.getTotalAmount());

        PaymentTransaction payment = new PaymentTransaction();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setProviderOrderId(gatewayOrder.providerOrderId());
        payment.setStatus(PaymentStatus.PENDING);

        PaymentTransaction saved = paymentDomain.save(payment);
        auditService.record(userId, "PAYMENT_INITIATED", "PAYMENT", saved.getId(), "Payment initiated");

        return new PaymentInitiateResponse(
                saved.getId(),
                order.getId(),
                saved.getProviderOrderId(),
                saved.getAmount(),
                paymentGateway.publicKey()
        );
    }

    @Transactional
    public void handleWebhook(String rawPayload, String signatureHeader) {
        if (!paymentGateway.verifyWebhookSignature(rawPayload, signatureHeader)) {
            throw new PaymentException("Invalid webhook signature", HttpStatus.UNAUTHORIZED);
        }

        PaymentWebhookPayload payload;
        try {
            payload = objectMapper.readValue(rawPayload, PaymentWebhookPayload.class);
        } catch (JsonProcessingException e) {
            throw new PaymentException("Malformed webhook payload", HttpStatus.BAD_REQUEST);
        }

        PaymentTransaction payment = paymentDomain.getByProviderOrderId(payload.providerOrderId());

        if (payload.amount() != null && payment.getAmount().compareTo(payload.amount()) != 0) {
            throw new PaymentException("Amount mismatch detected", HttpStatus.BAD_REQUEST);
        }

        payment.setProviderPaymentId(payload.providerPaymentId());
        payment.setRawGatewayPayload(rawPayload);

        if ("captured".equalsIgnoreCase(payload.status()) || "payment.captured".equalsIgnoreCase(payload.event())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(Instant.now());
            paymentDomain.save(payment);
            orderService.transitionBySystem(payment.getOrder().getUser().getId(), payment.getOrder().getId(), OrderStatus.PAYMENT_SUCCESS, "PAYMENT_WEBHOOK", "Payment captured");
            auditService.record(payment.getOrder().getUser().getId(), "PAYMENT_CONFIRMED", "PAYMENT", payment.getId(), "Webhook confirmed success");
            eventPublisher.publishEvent(new PaymentStatusChangedEvent(payment.getOrder().getId(), payment.getId(), PaymentStatus.SUCCESS, payment.getOrder().getUser().getId()));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentDomain.save(payment);
            orderService.transitionBySystem(payment.getOrder().getUser().getId(), payment.getOrder().getId(), OrderStatus.REJECTED, "PAYMENT_WEBHOOK", "Payment failed");
            auditService.record(payment.getOrder().getUser().getId(), "PAYMENT_FAILED", "PAYMENT", payment.getId(), "Webhook reported failure");
            eventPublisher.publishEvent(new PaymentStatusChangedEvent(payment.getOrder().getId(), payment.getId(), PaymentStatus.FAILED, payment.getOrder().getUser().getId()));
        }
    }

    @Transactional
    public PaymentDto refund(UUID actorUserId, UUID orderId, RefundRequest request) {
        PaymentTransaction payment = paymentDomain.getLatestByOrderId(orderId);
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Only successful payments can be refunded", HttpStatus.BAD_REQUEST);
        }

        PaymentGateway.GatewayRefund refund = paymentGateway.refund(payment.getProviderPaymentId(), payment.getAmount(), request.reason());
        if (!refund.success()) {
            throw new PaymentException("Gateway refund failed", HttpStatus.BAD_GATEWAY);
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(Instant.now());
        PaymentTransaction saved = paymentDomain.save(payment);

        orderService.transitionBySystem(actorUserId, orderId, OrderStatus.REFUNDED, "PAYMENT_REFUND", request.reason());
        auditService.record(actorUserId, "REFUND_INITIATED", "PAYMENT", saved.getId(), request.reason());
        eventPublisher.publishEvent(new PaymentStatusChangedEvent(saved.getOrder().getId(), saved.getId(), PaymentStatus.REFUNDED, actorUserId));
        return PaymentMapper.toDto(saved);
    }

    public PaymentDto latestByOrder(UUID orderId) {
        return PaymentMapper.toDto(paymentDomain.getLatestByOrderId(orderId));
    }
}
