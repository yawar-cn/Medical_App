package com.medapp.settlement.dto;

import com.medapp.settlement.entity.SettlementStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SettlementDto(
        UUID id,
        UUID orderId,
        UUID pharmacyId,
        UUID riderId,
        BigDecimal grossAmount,
        BigDecimal commissionPercentage,
        BigDecimal commissionAmount,
        BigDecimal pharmacyPayout,
        BigDecimal riderPayout,
        SettlementStatus status,
        Instant settledAt
) {
}
