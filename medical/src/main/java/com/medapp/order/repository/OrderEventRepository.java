package com.medapp.order.repository;

import com.medapp.order.entity.OrderEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventRepository extends JpaRepository<OrderEvent, UUID> {
    List<OrderEvent> findByOrderIdOrderByEventTimeAsc(UUID orderId);
}
