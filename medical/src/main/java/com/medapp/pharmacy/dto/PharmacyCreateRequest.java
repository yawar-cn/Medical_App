package com.medapp.pharmacy.dto;

import com.medapp.pharmacy.validation.ValidLicenseNumber;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

public record PharmacyCreateRequest(
        @NotBlank String storeName,
        @ValidLicenseNumber String licenseNumber,
        @NotBlank String kycDocumentPath,
        @NotBlank String address,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
        @NotNull LocalTime opensAt,
        @NotNull LocalTime closesAt
) {
}
