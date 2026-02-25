package com.medapp.delivery.dto;

import com.medapp.auth.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;

public record RiderRegistrationRequest(
        @NotBlank String fullName,
        @NotBlank @ValidPhone String phone
) {
}
