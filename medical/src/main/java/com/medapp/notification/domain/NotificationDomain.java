package com.medapp.notification.domain;

import com.medapp.notification.entity.NotificationLog;
import com.medapp.notification.repository.NotificationLogRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NotificationDomain {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationDomain(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    public NotificationLog save(NotificationLog log) {
        return notificationLogRepository.save(log);
    }

    public Page<NotificationLog> byUser(UUID userId, Pageable pageable) {
        return notificationLogRepository.findByUserId(userId, pageable);
    }
}
