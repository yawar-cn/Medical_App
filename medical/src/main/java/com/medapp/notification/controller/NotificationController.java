package com.medapp.notification.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.notification.dto.NotificationDto;
import com.medapp.notification.dto.NotificationRequest;
import com.medapp.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> send(@RequestBody @Valid NotificationRequest request) {
        notificationService.send(request);
        return ApiResponse.ok("Notification queued", null);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<PageResponse<NotificationDto>> mine(@AuthenticationPrincipal UUID userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Notifications fetched", notificationService.listByUser(userId, page, size));
    }
}
