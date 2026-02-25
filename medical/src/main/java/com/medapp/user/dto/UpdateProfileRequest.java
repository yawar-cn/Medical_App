package com.medapp.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @Email(message = "Email must be valid")
        String email
) {
}
