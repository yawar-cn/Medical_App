package com.medapp.cart.dto;

import com.medapp.cart.validation.ValidCartQuantity;

public record CartItemUpdateRequest(
        @ValidCartQuantity int quantity
) {
}
