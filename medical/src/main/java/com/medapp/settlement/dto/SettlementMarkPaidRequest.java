package com.medapp.settlement.dto;

import jakarta.validation.constraints.NotBlank;

public record SettlementMarkPaidRequest(
        @NotBlank String reference
) {
}
