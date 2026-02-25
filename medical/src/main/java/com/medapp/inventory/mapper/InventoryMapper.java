package com.medapp.inventory.mapper;

import com.medapp.inventory.dto.InventoryDto;
import com.medapp.inventory.entity.InventoryItem;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryDto toDto(InventoryItem item) {
        return new InventoryDto(
                item.getId(),
                item.getPharmacy().getId(),
                item.getMedicine().getId(),
                item.getMedicine().getName(),
                item.getBatchNumber(),
                item.getExpiryDate(),
                item.getQuantityAvailable(),
                item.getQuantityReserved(),
                item.getSellingPrice()
        );
    }
}
