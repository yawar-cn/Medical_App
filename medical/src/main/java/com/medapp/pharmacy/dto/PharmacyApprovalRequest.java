package com.medapp.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;

public record PharmacyApprovalRequest(
        boolean approved,
        @NotBlank String reason
) {
}
