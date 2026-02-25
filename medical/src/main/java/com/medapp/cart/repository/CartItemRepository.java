package com.medapp.cart.repository;

import com.medapp.cart.entity.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @EntityGraph(attributePaths = {"medicine", "pharmacy", "prescription"})
    List<CartItem> findByCartId(UUID cartId);

    Optional<CartItem> findByCartIdAndMedicineIdAndPharmacyId(UUID cartId, UUID medicineId, UUID pharmacyId);

    void deleteByCartId(UUID cartId);
}
