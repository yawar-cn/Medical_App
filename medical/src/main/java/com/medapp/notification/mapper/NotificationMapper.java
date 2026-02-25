package com.medapp.notification.mapper;

import com.medapp.notification.dto.NotificationDto;
import com.medapp.notification.entity.NotificationLog;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationDto toDto(NotificationLog log) {
        return new NotificationDto(
                log.getId(),
                log.getUserId(),
                log.getChannel(),
                log.getSubject(),
                log.getMessage(),
                log.getStatus(),
                log.getCreatedAt()
        );
    }
}
