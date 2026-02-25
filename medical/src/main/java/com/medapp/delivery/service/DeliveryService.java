package com.medapp.delivery.service;

import com.medapp.audit.service.AuditService;
import com.medapp.common.util.GeoUtils;
import com.medapp.delivery.domain.DeliveryDomain;
import com.medapp.delivery.dto.DeliveryAssignmentDto;
import com.medapp.delivery.dto.DeliveryOtpRequest;
import com.medapp.delivery.dto.RiderAvailabilityRequest;
import com.medapp.delivery.dto.RiderDto;
import com.medapp.delivery.dto.RiderLocationRequest;
import com.medapp.delivery.dto.RiderRegistrationRequest;
import com.medapp.delivery.entity.DeliveryAssignment;
import com.medapp.delivery.entity.DeliveryStatus;
import com.medapp.delivery.entity.RiderProfile;
import com.medapp.delivery.exception.DeliveryException;
import com.medapp.delivery.mapper.DeliveryMapper;
import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderStatus;
import com.medapp.order.service.OrderService;
import com.medapp.user.entity.User;
import com.medapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private static final BigDecimal DEFAULT_DELIVERY_EARNING = BigDecimal.valueOf(40);

    private final DeliveryDomain deliveryDomain;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final AuditService auditService;

    public DeliveryService(DeliveryDomain deliveryDomain,
                           UserRepository userRepository,
                           OrderService orderService,
                           AuditService auditService) {
        this.deliveryDomain = deliveryDomain;
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.auditService = auditService;
    }

    @Transactional
    public RiderDto register(UUID riderUserId, RiderRegistrationRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(riderUserId)
                .orElseThrow(() -> new DeliveryException("User not found", HttpStatus.NOT_FOUND));

        RiderProfile rider = new RiderProfile();
        rider.setUser(user);
        rider.setFullName(request.fullName());
        rider.setPhone(request.phone());
        rider.setAvailable(false);
        rider.setLatitude(0.0);
        rider.setLongitude(0.0);
        rider.setTotalEarnings(BigDecimal.ZERO);
        RiderProfile saved = deliveryDomain.saveRider(rider);

        auditService.record(riderUserId, "DELIVERY_RIDER_REGISTERED", "RIDER", saved.getId(), "Rider registered");
        return DeliveryMapper.toRiderDto(saved);
    }

    @Transactional
    public RiderDto updateAvailability(UUID riderUserId, RiderAvailabilityRequest request) {
        RiderProfile rider = deliveryDomain.riderByUser(riderUserId);
        rider.setAvailable(request.available());
        RiderProfile saved = deliveryDomain.saveRider(rider);
        auditService.record(riderUserId, "DELIVERY_RIDER_AVAILABILITY", "RIDER", saved.getId(), "Availability set to " + request.available());
        return DeliveryMapper.toRiderDto(saved);
    }

    @Transactional
    public RiderDto updateLocation(UUID riderUserId, RiderLocationRequest request) {
        RiderProfile rider = deliveryDomain.riderByUser(riderUserId);
        rider.setLatitude(request.latitude());
        rider.setLongitude(request.longitude());
        RiderProfile saved = deliveryDomain.saveRider(rider);
        return DeliveryMapper.toRiderDto(saved);
    }

    @Transactional
    public DeliveryAssignmentDto assignNearest(UUID actorUserId, UUID orderId) {
        Order order = orderService.getOrderEntity(orderId);
        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new DeliveryException("Order not ready for rider assignment", HttpStatus.BAD_REQUEST);
        }

        RiderProfile rider = deliveryDomain.availableRiders().stream()
                .min(Comparator.comparingDouble(r -> GeoUtils.distanceKm(
                        r.getLatitude(),
                        r.getLongitude(),
                        order.getPharmacy().getLatitude().doubleValue(),
                        order.getPharmacy().getLongitude().doubleValue())))
                .orElseThrow(() -> new DeliveryException("No available riders", HttpStatus.CONFLICT));

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setRider(rider);
        assignment.setStatus(DeliveryStatus.ASSIGNED);
        assignment.setEarningAmount(DEFAULT_DELIVERY_EARNING);
        assignment.setAssignedAt(Instant.now());
        DeliveryAssignment saved = deliveryDomain.saveAssignment(assignment);

        orderService.assignRider(actorUserId, orderId, rider.getId());
        rider.setAvailable(false);
        deliveryDomain.saveRider(rider);

        auditService.record(actorUserId, "DELIVERY_RIDER_ASSIGNED", "ORDER", orderId, "Rider assigned");
        return DeliveryMapper.toAssignmentDto(saved);
    }

    @Transactional
    public DeliveryAssignmentDto markOutForDelivery(UUID riderUserId, UUID orderId) {
        RiderProfile rider = deliveryDomain.riderByUser(riderUserId);
        DeliveryAssignment assignment = deliveryDomain.assignmentByOrder(orderId);
        if (!assignment.getRider().getId().equals(rider.getId())) {
            throw new DeliveryException("Order not assigned to rider", HttpStatus.FORBIDDEN);
        }
        assignment.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        DeliveryAssignment saved = deliveryDomain.saveAssignment(assignment);
        orderService.transitionBySystem(riderUserId, orderId, OrderStatus.OUT_FOR_DELIVERY, "DELIVERY", "Order picked by rider");
        return DeliveryMapper.toAssignmentDto(saved);
    }

    @Transactional
    public DeliveryAssignmentDto verifyOtpAndComplete(UUID riderUserId, UUID orderId, DeliveryOtpRequest request) {
        RiderProfile rider = deliveryDomain.riderByUser(riderUserId);
        DeliveryAssignment assignment = deliveryDomain.assignmentByOrder(orderId);
        if (!assignment.getRider().getId().equals(rider.getId())) {
            throw new DeliveryException("Order not assigned to rider", HttpStatus.FORBIDDEN);
        }

        orderService.verifyDeliveryOtp(rider.getId(), orderId, request.otp());
        assignment.setStatus(DeliveryStatus.DELIVERED);
        assignment.setDeliveredAt(Instant.now());

        rider.setTotalEarnings(rider.getTotalEarnings().add(assignment.getEarningAmount()));
        rider.setAvailable(true);
        deliveryDomain.saveRider(rider);

        DeliveryAssignment saved = deliveryDomain.saveAssignment(assignment);
        auditService.record(riderUserId, "DELIVERY_COMPLETED", "ORDER", orderId, "Delivery OTP verified");
        return DeliveryMapper.toAssignmentDto(saved);
    }

    public RiderDto me(UUID riderUserId) {
        return DeliveryMapper.toRiderDto(deliveryDomain.riderByUser(riderUserId));
    }
}
