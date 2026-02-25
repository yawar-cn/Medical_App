package com.medapp.delivery.mapper;

import com.medapp.delivery.dto.DeliveryAssignmentDto;
import com.medapp.delivery.dto.RiderDto;
import com.medapp.delivery.entity.DeliveryAssignment;
import com.medapp.delivery.entity.RiderProfile;

public final class DeliveryMapper {

    private DeliveryMapper() {
    }

    public static RiderDto toRiderDto(RiderProfile rider) {
        return new RiderDto(
                rider.getId(),
                rider.getUser().getId(),
                rider.getFullName(),
                rider.getPhone(),
                rider.isAvailable(),
                rider.getLatitude(),
                rider.getLongitude(),
                rider.getTotalEarnings()
        );
    }

    public static DeliveryAssignmentDto toAssignmentDto(DeliveryAssignment assignment) {
        return new DeliveryAssignmentDto(
                assignment.getId(),
                assignment.getOrder().getId(),
                assignment.getRider().getId(),
                assignment.getStatus(),
                assignment.getEarningAmount(),
                assignment.getAssignedAt(),
                assignment.getDeliveredAt()
        );
    }
}
