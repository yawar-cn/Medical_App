package com.medapp.auth.dto;

import com.medapp.auth.validation.ValidPhone;
import com.medapp.common.constants.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OtpLoginRequest(
        @NotNull UUID challengeId,
        @NotBlank @ValidPhone String phone,
        @NotBlank String otp,
        Role role
) {
}
