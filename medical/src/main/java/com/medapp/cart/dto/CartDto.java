package com.medapp.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(
        UUID cartId,
        UUID userId,
        List<CartItemDto> items,
        BigDecimal totalAmount
) {
}
