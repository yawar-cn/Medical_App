package com.medapp.delivery.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.delivery.dto.DeliveryAssignmentDto;
import com.medapp.delivery.dto.DeliveryOtpRequest;
import com.medapp.delivery.dto.RiderAvailabilityRequest;
import com.medapp.delivery.dto.RiderDto;
import com.medapp.delivery.dto.RiderLocationRequest;
import com.medapp.delivery.dto.RiderRegistrationRequest;
import com.medapp.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/riders/register")
    @PreAuthorize("hasRole('RIDER')")
    public ApiResponse<RiderDto> register(@AuthenticationPrincipal UUID riderUserId,
                                          @RequestBody @Valid RiderRegistrationRequest request) {
        return ApiResponse.ok("Rider registered", deliveryService.register(riderUserId, request));
    }

    @PostMapping("/riders/availability")
    @PreAuthorize("hasRole('RIDER')")
    public ApiResponse<RiderDto> availability(@AuthenticationPrincipal UUID riderUserId,
                                              @RequestBody RiderAvailabilityRequest request) {
        return ApiResponse.ok("Rider availability updated", deliveryService.updateAvailability(riderUserId, request));
    }

    @PostMapping("/riders/location")
    @PreAuthorize("hasRole('RIDER')")
    public ApiResponse<RiderDto> location(@AuthenticationPrincipal UUID riderUserId,
                                          @RequestBody @Valid RiderLocationRequest request) {
        return ApiResponse.ok("Rider location updated", deliveryService.updateLocation(riderUserId, request));
    }

    @GetMapping("/riders/me")
    @PreAuthorize("hasRole('RIDER')")
    public ApiResponse<RiderDto> me(@AuthenticationPrincipal UUID riderUserId) {
        return ApiResponse.ok("Rider profile fetched", deliveryService.me(riderUserId));
    }

    @PostMapping("/{orderId}/assign")
    @PreAuthorize("hasAnyRole('PHARMACY','ADMIN')")
    public ApiResponse<DeliveryAssignmentDto> assign(@AuthenticationPrincipal UUID actorUserId,
                                                     @PathVariable UUID orderId) {
        return ApiResponse.ok("Rider assigned", deliveryService.assignNearest(actorUserId, orderId));
    }

    @PostMapping("/{orderId}/out-for-delivery")
    @PreAuthorize("hasRole('RIDER')")
    public ApiResponse<DeliveryAssignmentDto> outForDelivery(@AuthenticationPrincipal UUID riderUserId,
                                                             @PathVariable UUID orderId) {
        return ApiResponse.ok("Order marked out for delivery", deliveryService.markOutForDelivery(riderUserId, orderId));
    }

    @PostMapping("/{orderId}/verify-otp")
    @PreAuthorize("hasRole('RIDER')")
    public ApiResponse<DeliveryAssignmentDto> verifyOtp(@AuthenticationPrincipal UUID riderUserId,
                                                        @PathVariable UUID orderId,
                                                        @RequestBody @Valid DeliveryOtpRequest request) {
        return ApiResponse.ok("Delivery completed", deliveryService.verifyOtpAndComplete(riderUserId, orderId, request));
    }
}
