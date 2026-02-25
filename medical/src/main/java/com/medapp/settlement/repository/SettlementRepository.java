package com.medapp.settlement.repository;

import com.medapp.settlement.entity.SettlementRecord;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<SettlementRecord, UUID> {
    Optional<SettlementRecord> findByOrderId(UUID orderId);

    Page<SettlementRecord> findByPharmacyId(UUID pharmacyId, Pageable pageable);

    Page<SettlementRecord> findByRiderId(UUID riderId, Pageable pageable);

    Page<SettlementRecord> findByCreatedAtBetween(Instant from, Instant to, Pageable pageable);
}
