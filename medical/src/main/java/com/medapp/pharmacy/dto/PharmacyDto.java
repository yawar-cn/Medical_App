package com.medapp.pharmacy.dto;

import com.medapp.pharmacy.entity.PharmacyStatus;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record PharmacyDto(
        UUID id,
        UUID ownerUserId,
        String storeName,
        String licenseNumber,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalTime opensAt,
        LocalTime closesAt,
        PharmacyStatus status,
        String rejectionReason
) {
}
