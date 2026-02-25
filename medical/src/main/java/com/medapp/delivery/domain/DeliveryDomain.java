package com.medapp.delivery.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.delivery.entity.DeliveryAssignment;
import com.medapp.delivery.entity.DeliveryStatus;
import com.medapp.delivery.entity.RiderProfile;
import com.medapp.delivery.repository.DeliveryAssignmentRepository;
import com.medapp.delivery.repository.RiderProfileRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DeliveryDomain {

    private final RiderProfileRepository riderProfileRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;

    public DeliveryDomain(RiderProfileRepository riderProfileRepository,
                          DeliveryAssignmentRepository deliveryAssignmentRepository) {
        this.riderProfileRepository = riderProfileRepository;
        this.deliveryAssignmentRepository = deliveryAssignmentRepository;
    }

    public RiderProfile saveRider(RiderProfile rider) {
        return riderProfileRepository.save(rider);
    }

    public RiderProfile riderByUser(UUID userId) {
        return riderProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Rider profile not found"));
    }

    public List<RiderProfile> availableRiders() {
        return riderProfileRepository.findByAvailableTrue();
    }

    public DeliveryAssignment saveAssignment(DeliveryAssignment assignment) {
        return deliveryAssignmentRepository.save(assignment);
    }

    public DeliveryAssignment assignmentByOrder(UUID orderId) {
        return deliveryAssignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Delivery assignment not found"));
    }

    public List<DeliveryAssignment> activeByRider(UUID riderId) {
        return deliveryAssignmentRepository.findByRiderIdAndStatus(riderId, DeliveryStatus.OUT_FOR_DELIVERY);
    }
}
