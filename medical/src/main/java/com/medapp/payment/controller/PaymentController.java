package com.medapp.payment.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.payment.dto.PaymentDto;
import com.medapp.payment.dto.PaymentInitiateRequest;
import com.medapp.payment.dto.PaymentInitiateResponse;
import com.medapp.payment.dto.RefundRequest;
import com.medapp.payment.service.PaymentService;
import com.medapp.payment.validation.ValidWebhookSignature;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PaymentInitiateResponse> initiate(@AuthenticationPrincipal UUID userId,
                                                         @RequestBody @Valid PaymentInitiateRequest request) {
        return ApiResponse.ok("Payment initiated", paymentService.initiate(userId, request));
    }

    @PostMapping("/webhook")
    public ApiResponse<Void> webhook(@RequestBody String payload,
                                     @RequestHeader("X-Razorpay-Signature") @ValidWebhookSignature String signature) {
        paymentService.handleWebhook(payload, signature);
        return ApiResponse.ok("Webhook processed", null);
    }

    @PostMapping("/{orderId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentDto> refund(@AuthenticationPrincipal UUID actorUserId,
                                          @PathVariable UUID orderId,
                                          @RequestBody @Valid RefundRequest request) {
        return ApiResponse.ok("Refund processed", paymentService.refund(actorUserId, orderId, request));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','ADMIN')")
    public ApiResponse<PaymentDto> latest(@PathVariable UUID orderId) {
        return ApiResponse.ok("Payment fetched", paymentService.latestByOrder(orderId));
    }
}
