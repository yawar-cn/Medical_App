package com.medapp.settlement.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.settlement.entity.SettlementRecord;
import com.medapp.settlement.repository.SettlementRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SettlementDomain {

    private final SettlementRepository settlementRepository;

    public SettlementDomain(SettlementRepository settlementRepository) {
        this.settlementRepository = settlementRepository;
    }

    public SettlementRecord save(SettlementRecord record) {
        return settlementRepository.save(record);
    }

    public SettlementRecord getByOrder(UUID orderId) {
        return settlementRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Settlement not found for order"));
    }

    public boolean existsByOrder(UUID orderId) {
        return settlementRepository.findByOrderId(orderId).isPresent();
    }

    public Page<SettlementRecord> all(Pageable pageable) {
        return settlementRepository.findAll(pageable);
    }

    public Page<SettlementRecord> byPharmacy(UUID pharmacyId, Pageable pageable) {
        return settlementRepository.findByPharmacyId(pharmacyId, pageable);
    }

    public Page<SettlementRecord> byRider(UUID riderId, Pageable pageable) {
        return settlementRepository.findByRiderId(riderId, pageable);
    }

    public Page<SettlementRecord> byDateRange(Instant from, Instant to, Pageable pageable) {
        return settlementRepository.findByCreatedAtBetween(from, to, pageable);
    }
}
