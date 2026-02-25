package com.medapp.notification.dto;

import com.medapp.notification.entity.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationRequest(
        @NotNull UUID userId,
        @NotNull NotificationChannel channel,
        @NotBlank String subject,
        @NotBlank String message
) {
}
