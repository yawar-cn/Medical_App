package com.medapp.delivery.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RiderDto(
        UUID id,
        UUID userId,
        String fullName,
        String phone,
        boolean available,
        double latitude,
        double longitude,
        BigDecimal totalEarnings
) {
}
