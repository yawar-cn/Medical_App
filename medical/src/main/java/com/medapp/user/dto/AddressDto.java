package com.medapp.user.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AddressDto(
        UUID id,
        String label,
        String line1,
        String line2,
        String city,
        String state,
        String pincode,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean defaultAddress
) {
}
