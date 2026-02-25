package com.medapp.notification.dto;

import com.medapp.notification.entity.NotificationChannel;
import com.medapp.notification.entity.NotificationStatus;
import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID userId,
        NotificationChannel channel,
        String subject,
        String message,
        NotificationStatus status,
        Instant createdAt
) {
}
