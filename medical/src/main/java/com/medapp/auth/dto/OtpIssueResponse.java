package com.medapp.auth.dto;

import java.util.UUID;

public record OtpIssueResponse(
        UUID challengeId,
        String maskedPhone,
        String mockOtp
) {
}
