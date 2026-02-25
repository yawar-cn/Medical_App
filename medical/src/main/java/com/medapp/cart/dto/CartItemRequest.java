package com.medapp.cart.dto;

import com.medapp.cart.validation.ValidCartQuantity;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CartItemRequest(
        @NotNull UUID pharmacyId,
        @NotNull UUID medicineId,
        UUID prescriptionId,
        @ValidCartQuantity int quantity
) {
}
