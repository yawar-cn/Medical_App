package com.medapp.inventory.service;

import com.medapp.audit.service.AuditService;
import com.medapp.common.exception.NotFoundException;
import com.medapp.inventory.domain.InventoryDomain;
import com.medapp.inventory.dto.InventoryDto;
import com.medapp.inventory.dto.InventoryUpsertRequest;
import com.medapp.inventory.dto.StockReservationRequest;
import com.medapp.inventory.dto.StockValidationResponse;
import com.medapp.inventory.entity.InventoryItem;
import com.medapp.inventory.entity.ReservationStatus;
import com.medapp.inventory.entity.StockReservation;
import com.medapp.inventory.exception.InventoryException;
import com.medapp.inventory.mapper.InventoryMapper;
import com.medapp.medicine.entity.Medicine;
import com.medapp.medicine.repository.MedicineRepository;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.repository.PharmacyRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private static final long LOCK_TTL_SECONDS = 30;

    private final InventoryDomain inventoryDomain;
    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;
    private final StringRedisTemplate redisTemplate;
    private final AuditService auditService;

    public InventoryService(InventoryDomain inventoryDomain,
                            PharmacyRepository pharmacyRepository,
                            MedicineRepository medicineRepository,
                            StringRedisTemplate redisTemplate,
                            AuditService auditService) {
        this.inventoryDomain = inventoryDomain;
        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
        this.redisTemplate = redisTemplate;
        this.auditService = auditService;
    }

    @Transactional
    public InventoryDto upsert(UUID actorId, UUID pharmacyId, InventoryUpsertRequest request) {
        Pharmacy pharmacy = pharmacyRepository.findByIdAndDeletedAtIsNull(pharmacyId)
                .orElseThrow(() -> new NotFoundException("Pharmacy not found"));
        Medicine medicine = medicineRepository.findByIdAndDeletedAtIsNull(request.medicineId())
                .orElseThrow(() -> new NotFoundException("Medicine not found"));

        InventoryItem item = inventoryDomain.getLockedItem(pharmacyId, request.medicineId(), request.batchNumber());
        item.setExpiryDate(request.expiryDate());
        item.setQuantityAvailable(request.quantity());
        item.setSellingPrice(request.sellingPrice());
        InventoryItem saved = inventoryDomain.saveItem(item);

        auditService.record(actorId, "INVENTORY_UPDATED", "INVENTORY_ITEM", saved.getId(), "Inventory upsert completed");
        return InventoryMapper.toDto(saved);
    }

    @Transactional
    public InventoryDto create(UUID actorId, UUID pharmacyId, InventoryUpsertRequest request) {
        Pharmacy pharmacy = pharmacyRepository.findByIdAndDeletedAtIsNull(pharmacyId)
                .orElseThrow(() -> new NotFoundException("Pharmacy not found"));
        Medicine medicine = medicineRepository.findByIdAndDeletedAtIsNull(request.medicineId())
                .orElseThrow(() -> new NotFoundException("Medicine not found"));

        InventoryItem item = new InventoryItem();
        item.setPharmacy(pharmacy);
        item.setMedicine(medicine);
        item.setBatchNumber(request.batchNumber());
        item.setExpiryDate(request.expiryDate());
        item.setQuantityAvailable(request.quantity());
        item.setQuantityReserved(0);
        item.setSellingPrice(request.sellingPrice());

        InventoryItem saved = inventoryDomain.saveItem(item);
        auditService.record(actorId, "INVENTORY_UPDATED", "INVENTORY_ITEM", saved.getId(), "Inventory created");
        return InventoryMapper.toDto(saved);
    }

    public List<InventoryDto> list(UUID pharmacyId) {
        return inventoryDomain.listByPharmacy(pharmacyId).stream().map(InventoryMapper::toDto).toList();
    }

    public StockValidationResponse validateStock(UUID pharmacyId, UUID medicineId, int quantity) {
        try {
            InventoryItem item = inventoryDomain.getFirstValidBatch(pharmacyId, medicineId);
            if (item.getQuantityAvailable() < quantity || item.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                return new StockValidationResponse(pharmacyId, medicineId, false, "Insufficient available stock");
            }
            return new StockValidationResponse(pharmacyId, medicineId, true, "Stock available");
        } catch (NotFoundException ex) {
            return new StockValidationResponse(pharmacyId, medicineId, false, "Stock not found");
        }
    }

    @Transactional
    public void reserveStock(UUID actorId, UUID orderId, UUID pharmacyId, List<StockReservationRequest> items) {
        List<String> lockKeys = new ArrayList<>();
        try {
            for (StockReservationRequest line : items) {
                String lockKey = lockKey(pharmacyId, line.medicineId());
                Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, orderId.toString(), LOCK_TTL_SECONDS, TimeUnit.SECONDS);
                if (!Boolean.TRUE.equals(acquired)) {
                    throw new InventoryException("Inventory currently busy, retry", HttpStatus.CONFLICT);
                }
                lockKeys.add(lockKey);

                InventoryItem item = inventoryDomain.getFirstValidBatch(pharmacyId, line.medicineId());
                if (item.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                    throw new InventoryException("Stock expired for medicine: " + line.medicineId(), HttpStatus.BAD_REQUEST);
                }
                if (item.getQuantityAvailable() < line.quantity()) {
                    throw new InventoryException("Insufficient stock for medicine: " + line.medicineId(), HttpStatus.BAD_REQUEST);
                }
                item.setQuantityAvailable(item.getQuantityAvailable() - line.quantity());
                item.setQuantityReserved(item.getQuantityReserved() + line.quantity());
                inventoryDomain.saveItem(item);

                StockReservation reservation = new StockReservation();
                reservation.setOrderId(orderId);
                reservation.setPharmacyId(pharmacyId);
                reservation.setMedicineId(line.medicineId());
                reservation.setQuantity(line.quantity());
                reservation.setStatus(ReservationStatus.LOCKED);
                reservation.setExpiresAt(Instant.now().plusSeconds(900));
                inventoryDomain.saveReservation(reservation);
                auditService.record(actorId, "INVENTORY_RESERVED", "ORDER", orderId, "Reserved qty=" + line.quantity());
            }
        } finally {
            lockKeys.forEach(redisTemplate::delete);
        }
    }

    @Transactional
    public void confirmReservation(UUID actorId, UUID orderId) {
        List<StockReservation> reservations = inventoryDomain.findLockedReservations(orderId);
        for (StockReservation reservation : reservations) {
            InventoryItem item = inventoryDomain.getFirstValidBatch(reservation.getPharmacyId(), reservation.getMedicineId());
            item.setQuantityReserved(item.getQuantityReserved() - reservation.getQuantity());
            inventoryDomain.saveItem(item);

            reservation.setStatus(ReservationStatus.CONFIRMED);
            inventoryDomain.saveReservation(reservation);
            auditService.record(actorId, "INVENTORY_CONFIRMED", "ORDER", orderId, "Stock confirmed");
        }
    }

    @Transactional
    public void releaseReservation(UUID actorId, UUID orderId) {
        List<StockReservation> reservations = inventoryDomain.findLockedReservations(orderId);
        for (StockReservation reservation : reservations) {
            InventoryItem item = inventoryDomain.getFirstValidBatch(reservation.getPharmacyId(), reservation.getMedicineId());
            item.setQuantityAvailable(item.getQuantityAvailable() + reservation.getQuantity());
            item.setQuantityReserved(item.getQuantityReserved() - reservation.getQuantity());
            inventoryDomain.saveItem(item);

            reservation.setStatus(ReservationStatus.RELEASED);
            inventoryDomain.saveReservation(reservation);
            auditService.record(actorId, "INVENTORY_RELEASED", "ORDER", orderId, "Stock released");
        }
    }

    private String lockKey(UUID pharmacyId, UUID medicineId) {
        return "inv-lock:" + pharmacyId + ":" + medicineId;
    }
}
