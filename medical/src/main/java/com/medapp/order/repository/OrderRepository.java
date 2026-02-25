package com.medapp.order.repository;

import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"pharmacy", "prescription", "user"})
    Optional<Order> findById(UUID id);

    @EntityGraph(attributePaths = {"pharmacy"})
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Page<Order> findByPharmacyIdAndStatus(UUID pharmacyId, OrderStatus status, Pageable pageable);
}
