package com.medapp.order.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.order.dto.CreateOrderRequest;
import com.medapp.order.dto.OrderDto;
import com.medapp.order.dto.OrderStatusTransitionRequest;
import com.medapp.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<OrderDto> create(@AuthenticationPrincipal UUID userId,
                                        @RequestBody @Valid CreateOrderRequest request) {
        return ApiResponse.ok("Order created", orderService.create(userId, request));
    }

    @PostMapping("/{orderId}/transition")
    @PreAuthorize("hasAnyRole('PHARMACY','RIDER','ADMIN')")
    public ApiResponse<OrderDto> transition(@AuthenticationPrincipal UUID actorUserId,
                                            @PathVariable UUID orderId,
                                            @RequestBody @Valid OrderStatusTransitionRequest request) {
        return ApiResponse.ok("Order status updated", orderService.transition(actorUserId, orderId, request, "API"));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<OrderDto> get(@PathVariable UUID orderId) {
        return ApiResponse.ok("Order fetched", orderService.get(orderId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PageResponse<OrderDto>> history(@AuthenticationPrincipal UUID userId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Order history fetched", orderService.history(userId, page, size));
    }
}
