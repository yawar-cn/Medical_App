package com.medapp.prescription.dto;

import jakarta.validation.constraints.NotBlank;

public record PrescriptionReviewRequest(
        boolean approved,
        @NotBlank String notes
) {
}
