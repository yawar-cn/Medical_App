package com.medapp.payment.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.payment.entity.PaymentTransaction;
import com.medapp.payment.repository.PaymentTransactionRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentDomain {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentDomain(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public PaymentTransaction save(PaymentTransaction transaction) {
        return paymentTransactionRepository.save(transaction);
    }

    public PaymentTransaction getLatestByOrderId(UUID orderId) {
        return paymentTransactionRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new NotFoundException("Payment not found for order"));
    }

    public PaymentTransaction getByProviderOrderId(String providerOrderId) {
        return paymentTransactionRepository.findByProviderOrderId(providerOrderId)
                .orElseThrow(() -> new NotFoundException("Payment not found for provider order"));
    }
}
