package com.medapp.delivery.repository;

import com.medapp.delivery.entity.DeliveryAssignment;
import com.medapp.delivery.entity.DeliveryStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, UUID> {
    Optional<DeliveryAssignment> findByOrderId(UUID orderId);

    List<DeliveryAssignment> findByRiderIdAndStatus(UUID riderId, DeliveryStatus status);
}
