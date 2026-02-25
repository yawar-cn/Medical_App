package com.medapp.cart.repository;

import com.medapp.cart.entity.Cart;
import com.medapp.cart.entity.CartStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);

    Optional<Cart> findByIdAndUserId(UUID id, UUID userId);
}
