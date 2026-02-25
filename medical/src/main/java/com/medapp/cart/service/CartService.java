package com.medapp.cart.service;

import com.medapp.audit.service.AuditService;
import com.medapp.cart.domain.CartDomain;
import com.medapp.cart.dto.CartDto;
import com.medapp.cart.dto.CartItemRequest;
import com.medapp.cart.dto.CartItemUpdateRequest;
import com.medapp.cart.entity.Cart;
import com.medapp.cart.entity.CartItem;
import com.medapp.cart.exception.CartException;
import com.medapp.cart.mapper.CartMapper;
import com.medapp.common.exception.NotFoundException;
import com.medapp.inventory.service.InventoryService;
import com.medapp.medicine.entity.Medicine;
import com.medapp.medicine.repository.MedicineRepository;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.repository.PharmacyRepository;
import com.medapp.prescription.entity.Prescription;
import com.medapp.prescription.repository.PrescriptionRepository;
import com.medapp.prescription.service.PrescriptionService;
import com.medapp.user.entity.User;
import com.medapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartDomain cartDomain;
    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;
    private final InventoryService inventoryService;
    private final PrescriptionService prescriptionService;
    private final PrescriptionRepository prescriptionRepository;
    private final AuditService auditService;

    public CartService(CartDomain cartDomain,
                       UserRepository userRepository,
                       PharmacyRepository pharmacyRepository,
                       MedicineRepository medicineRepository,
                       InventoryService inventoryService,
                       PrescriptionService prescriptionService,
                       PrescriptionRepository prescriptionRepository,
                       AuditService auditService) {
        this.cartDomain = cartDomain;
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
        this.inventoryService = inventoryService;
        this.prescriptionService = prescriptionService;
        this.prescriptionRepository = prescriptionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public CartDto addItem(UUID userId, CartItemRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = cartDomain.getOrCreateActiveCart(user);

        Pharmacy pharmacy = pharmacyRepository.findByIdAndDeletedAtIsNull(request.pharmacyId())
                .orElseThrow(() -> new NotFoundException("Pharmacy not found"));
        Medicine medicine = medicineRepository.findByIdAndDeletedAtIsNull(request.medicineId())
                .orElseThrow(() -> new NotFoundException("Medicine not found"));

        if (!inventoryService.validateStock(pharmacy.getId(), medicine.getId(), request.quantity()).available()) {
            throw new CartException("Insufficient stock", HttpStatus.BAD_REQUEST);
        }

        Prescription prescription = null;
        if (medicine.isPrescriptionRequired()) {
            if (request.prescriptionId() == null) {
                throw new CartException("Prescription required", HttpStatus.BAD_REQUEST);
            }
            prescriptionService.assertApproved(request.prescriptionId(), userId);
            prescription = prescriptionRepository.findById(request.prescriptionId())
                    .orElseThrow(() -> new NotFoundException("Prescription not found"));
        }

        CartItem item;
        try {
            item = cartDomain.getItem(cart.getId(), medicine.getId(), pharmacy.getId());
            item.setQuantity(request.quantity());
            item.setUnitPrice(medicine.getMrp());
            item.setPrescription(prescription);
        } catch (NotFoundException ex) {
            item = new CartItem();
            item.setCart(cart);
            item.setPharmacy(pharmacy);
            item.setMedicine(medicine);
            item.setQuantity(request.quantity());
            item.setUnitPrice(medicine.getMrp());
            item.setPrescription(prescription);
        }

        cartDomain.saveItem(item);
        auditService.record(userId, "ORDER_CART_UPDATED", "CART", cart.getId(), "Cart item add/update");
        return getCart(userId);
    }

    @Transactional
    public CartDto updateItem(UUID userId, UUID medicineId, UUID pharmacyId, CartItemUpdateRequest request) {
        Cart cart = cartDomain.getActiveCartByUserId(userId);
        CartItem item = cartDomain.getItem(cart.getId(), medicineId, pharmacyId);
        if (!inventoryService.validateStock(pharmacyId, medicineId, request.quantity()).available()) {
            throw new CartException("Insufficient stock", HttpStatus.BAD_REQUEST);
        }
        item.setQuantity(request.quantity());
        cartDomain.saveItem(item);
        auditService.record(userId, "ORDER_CART_UPDATED", "CART", cart.getId(), "Cart quantity updated");
        return getCart(userId);
    }

    @Transactional
    public CartDto removeItem(UUID userId, UUID medicineId, UUID pharmacyId) {
        Cart cart = cartDomain.getActiveCartByUserId(userId);
        CartItem item = cartDomain.getItem(cart.getId(), medicineId, pharmacyId);
        cartDomain.deleteItem(item);
        auditService.record(userId, "ORDER_CART_UPDATED", "CART", cart.getId(), "Cart item removed");
        return getCart(userId);
    }

    public CartDto getCart(UUID userId) {
        Cart cart = cartDomain.getActiveCartByUserId(userId);
        List<CartItem> items = cartDomain.items(cart.getId());
        return CartMapper.toDto(cart, items);
    }

    @Transactional
    public void clear(UUID userId) {
        Cart cart = cartDomain.getActiveCartByUserId(userId);
        cartDomain.clear(cart.getId());
        auditService.record(userId, "ORDER_CART_CLEARED", "CART", cart.getId(), "Cart cleared");
    }

    public List<CartItem> getActiveCartItems(UUID userId) {
        Cart cart = cartDomain.getActiveCartByUserId(userId);
        return cartDomain.items(cart.getId());
    }
}
