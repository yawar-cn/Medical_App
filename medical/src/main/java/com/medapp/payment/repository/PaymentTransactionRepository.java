package com.medapp.payment.repository;

import com.medapp.payment.entity.PaymentTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findTopByOrderIdOrderByCreatedAtDesc(UUID orderId);

    Optional<PaymentTransaction> findByProviderOrderId(String providerOrderId);
}
