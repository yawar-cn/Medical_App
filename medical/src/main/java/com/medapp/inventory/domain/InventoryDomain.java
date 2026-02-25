package com.medapp.inventory.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.inventory.entity.InventoryItem;
import com.medapp.inventory.entity.ReservationStatus;
import com.medapp.inventory.entity.StockReservation;
import com.medapp.inventory.repository.InventoryItemRepository;
import com.medapp.inventory.repository.StockReservationRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class InventoryDomain {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockReservationRepository stockReservationRepository;

    public InventoryDomain(InventoryItemRepository inventoryItemRepository,
                           StockReservationRepository stockReservationRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.stockReservationRepository = stockReservationRepository;
    }

    public InventoryItem saveItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    public InventoryItem getLockedItem(UUID pharmacyId, UUID medicineId, String batchNumber) {
        return inventoryItemRepository.findByPharmacyIdAndMedicineIdAndBatchNumber(pharmacyId, medicineId, batchNumber)
                .orElseThrow(() -> new NotFoundException("Inventory item not found"));
    }

    public InventoryItem getFirstValidBatch(UUID pharmacyId, UUID medicineId) {
        return inventoryItemRepository.findFirstByPharmacyIdAndMedicineIdOrderByExpiryDateAsc(pharmacyId, medicineId)
                .orElseThrow(() -> new NotFoundException("Medicine stock not found in pharmacy"));
    }

    public List<InventoryItem> listByPharmacy(UUID pharmacyId) {
        return inventoryItemRepository.findByPharmacyId(pharmacyId);
    }

    public StockReservation saveReservation(StockReservation reservation) {
        return stockReservationRepository.save(reservation);
    }

    public List<StockReservation> findLockedReservations(UUID orderId) {
        return stockReservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.LOCKED);
    }

    public void cleanupExpiredReservations() {
        stockReservationRepository.deleteByStatusAndExpiresAtBefore(ReservationStatus.LOCKED, Instant.now());
    }
}
