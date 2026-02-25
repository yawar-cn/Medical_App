package com.medapp.cart.mapper;

import com.medapp.cart.dto.CartDto;
import com.medapp.cart.dto.CartItemDto;
import com.medapp.cart.entity.Cart;
import com.medapp.cart.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemDto toItemDto(CartItem item) {
        return new CartItemDto(
                item.getId(),
                item.getPharmacy().getId(),
                item.getMedicine().getId(),
                item.getMedicine().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getPrescription() == null ? null : item.getPrescription().getId(),
                item.getMedicine().isPrescriptionRequired()
        );
    }

    public static CartDto toDto(Cart cart, List<CartItem> items) {
        List<CartItemDto> cartItems = items.stream().map(CartMapper::toItemDto).toList();
        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto(cart.getId(), cart.getUser().getId(), cartItems, total);
    }
}
