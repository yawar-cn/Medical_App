package com.medapp.cart.controller;

import com.medapp.cart.dto.CartDto;
import com.medapp.cart.dto.CartItemRequest;
import com.medapp.cart.dto.CartItemUpdateRequest;
import com.medapp.cart.service.CartService;
import com.medapp.common.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<CartDto> addItem(@AuthenticationPrincipal UUID userId,
                                        @RequestBody @Valid CartItemRequest request) {
        return ApiResponse.ok("Cart updated", cartService.addItem(userId, request));
    }

    @PatchMapping("/items/{pharmacyId}/{medicineId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<CartDto> updateItem(@AuthenticationPrincipal UUID userId,
                                           @PathVariable UUID pharmacyId,
                                           @PathVariable UUID medicineId,
                                           @RequestBody @Valid CartItemUpdateRequest request) {
        return ApiResponse.ok("Cart updated", cartService.updateItem(userId, medicineId, pharmacyId, request));
    }

    @DeleteMapping("/items/{pharmacyId}/{medicineId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<CartDto> removeItem(@AuthenticationPrincipal UUID userId,
                                           @PathVariable UUID pharmacyId,
                                           @PathVariable UUID medicineId) {
        return ApiResponse.ok("Cart updated", cartService.removeItem(userId, medicineId, pharmacyId));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<CartDto> getCart(@AuthenticationPrincipal UUID userId) {
        return ApiResponse.ok("Cart fetched", cartService.getCart(userId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> clearCart(@AuthenticationPrincipal UUID userId) {
        cartService.clear(userId);
        return ApiResponse.ok("Cart cleared", null);
    }
}
