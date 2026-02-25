package com.medapp.inventory.repository;

import com.medapp.inventory.entity.InventoryItem;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    Optional<InventoryItem> findFirstByPharmacyIdAndMedicineIdOrderByExpiryDateAsc(UUID pharmacyId, UUID medicineId);

    List<InventoryItem> findByPharmacyId(UUID pharmacyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItem> findByPharmacyIdAndMedicineIdAndBatchNumber(UUID pharmacyId, UUID medicineId, String batchNumber);
}
