package com.medapp.auth.dto;

import com.medapp.auth.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @NotBlank @ValidPhone String phone
) {
}
