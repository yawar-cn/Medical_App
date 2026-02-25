package com.medapp.user.dto;

import com.medapp.user.validation.ValidGeoCoordinate;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@ValidGeoCoordinate
public record AddressRequest(
        @NotBlank(message = "Label is required")
        String label,

        @NotBlank(message = "Address line1 is required")
        String line1,

        String line2,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Pincode is required")
        String pincode,

        @NotNull
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        BigDecimal latitude,

        @NotNull
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        BigDecimal longitude,

        boolean defaultAddress
) {
}
