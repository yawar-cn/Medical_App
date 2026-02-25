package com.medapp.notification.service;

import com.medapp.common.dto.PageResponse;
import com.medapp.common.event.OrderStatusChangedEvent;
import com.medapp.common.event.PaymentStatusChangedEvent;
import com.medapp.notification.domain.NotificationDomain;
import com.medapp.notification.dto.NotificationDto;
import com.medapp.notification.dto.NotificationRequest;
import com.medapp.notification.entity.NotificationChannel;
import com.medapp.notification.entity.NotificationLog;
import com.medapp.notification.entity.NotificationStatus;
import com.medapp.notification.mapper.NotificationMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationDomain notificationDomain;

    public NotificationService(NotificationDomain notificationDomain) {
        this.notificationDomain = notificationDomain;
    }

    @Async("notificationExecutor")
    public void send(NotificationRequest request) {
        NotificationStatus status;
        try {
            log.info("Sending {} notification to user={} subject={}", request.channel(), request.userId(), request.subject());
            status = NotificationStatus.SENT;
        } catch (Exception ex) {
            status = NotificationStatus.FAILED;
        }

        NotificationLog logEntity = new NotificationLog();
        logEntity.setUserId(request.userId());
        logEntity.setChannel(request.channel());
        logEntity.setSubject(request.subject());
        logEntity.setMessage(request.message());
        logEntity.setStatus(status);
        notificationDomain.save(logEntity);
    }

    @EventListener
    public void onOrderStatusChange(OrderStatusChangedEvent event) {
        send(new NotificationRequest(
                event.userId(),
                NotificationChannel.PUSH,
                "Order Update",
                "Order " + event.orderId() + " changed from " + event.from() + " to " + event.to()
        ));
    }

    @EventListener
    public void onPaymentStatusChange(PaymentStatusChangedEvent event) {
        send(new NotificationRequest(
                event.userId(),
                NotificationChannel.SMS,
                "Payment Update",
                "Payment for order " + event.orderId() + " is now " + event.status()
        ));
    }

    public PageResponse<NotificationDto> listByUser(UUID userId, int page, int size) {
        Page<NotificationDto> data = notificationDomain.byUser(userId, PageRequest.of(page, size)).map(NotificationMapper::toDto);
        return new PageResponse<>(data.getContent(), data.getNumber(), data.getSize(), data.getTotalElements(), data.getTotalPages(), data.isLast());
    }
}
