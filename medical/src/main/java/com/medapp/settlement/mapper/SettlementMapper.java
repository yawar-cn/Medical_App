package com.medapp.settlement.mapper;

import com.medapp.settlement.dto.SettlementDto;
import com.medapp.settlement.entity.SettlementRecord;

public final class SettlementMapper {

    private SettlementMapper() {
    }

    public static SettlementDto toDto(SettlementRecord settlement) {
        return new SettlementDto(
                settlement.getId(),
                settlement.getOrder().getId(),
                settlement.getPharmacyId(),
                settlement.getRiderId(),
                settlement.getGrossAmount(),
                settlement.getCommissionPercentage(),
                settlement.getCommissionAmount(),
                settlement.getPharmacyPayout(),
                settlement.getRiderPayout(),
                settlement.getStatus(),
                settlement.getSettledAt()
        );
    }
}
