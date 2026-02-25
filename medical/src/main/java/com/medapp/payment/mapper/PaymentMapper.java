package com.medapp.payment.mapper;

import com.medapp.payment.dto.PaymentDto;
import com.medapp.payment.entity.PaymentTransaction;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentDto toDto(PaymentTransaction payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getProviderOrderId(),
                payment.getProviderPaymentId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCompletedAt(),
                payment.getRefundedAt()
        );
    }
}
