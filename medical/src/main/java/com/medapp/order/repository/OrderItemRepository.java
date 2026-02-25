package com.medapp.order.repository;

import com.medapp.order.entity.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    @EntityGraph(attributePaths = {"medicine"})
    List<OrderItem> findByOrderId(UUID orderId);
}
