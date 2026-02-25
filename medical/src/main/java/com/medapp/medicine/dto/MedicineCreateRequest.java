package com.medapp.medicine.dto;

import com.medapp.medicine.validation.ValidGstPercentage;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MedicineCreateRequest(
        @NotBlank String name,
        @NotBlank String genericName,
        @NotBlank String manufacturer,
        @NotBlank String category,
        @NotNull @ValidGstPercentage BigDecimal gstPercentage,
        boolean prescriptionRequired,
        @NotNull @DecimalMin("0.01") BigDecimal mrp
) {
}
