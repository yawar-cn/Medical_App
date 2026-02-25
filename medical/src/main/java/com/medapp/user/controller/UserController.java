package com.medapp.user.controller;

import com.medapp.common.dto.ApiResponse;
import com.medapp.common.dto.PageResponse;
import com.medapp.order.dto.OrderDto;
import com.medapp.user.dto.AddressDto;
import com.medapp.user.dto.AddressRequest;
import com.medapp.user.dto.UpdateProfileRequest;
import com.medapp.user.dto.UserProfileDto;
import com.medapp.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<UserProfileDto> me(@AuthenticationPrincipal UUID userId) {
        return ApiResponse.ok("User profile fetched", userService.getProfile(userId));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<UserProfileDto> update(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(userId, request));
    }

    @PostMapping("/me/addresses")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<AddressDto> addAddress(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid AddressRequest request
    ) {
        return ApiResponse.ok("Address added", userService.addAddress(userId, request));
    }

    @GetMapping("/me/addresses")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<List<AddressDto>> listAddresses(@AuthenticationPrincipal UUID userId) {
        return ApiResponse.ok("Address list fetched", userService.getAddresses(userId));
    }

    @GetMapping("/me/orders")
    @PreAuthorize("hasAnyRole('USER','PHARMACY','RIDER','ADMIN')")
    public ApiResponse<PageResponse<OrderDto>> orderHistory(@AuthenticationPrincipal UUID userId,
                                                            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                                                            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Order history fetched", userService.getOrderHistory(userId, page, size));
    }
}
