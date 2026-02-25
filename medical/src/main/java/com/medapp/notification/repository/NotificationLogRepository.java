package com.medapp.notification.repository;

import com.medapp.notification.entity.NotificationLog;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    Page<NotificationLog> findByUserId(UUID userId, Pageable pageable);
}
