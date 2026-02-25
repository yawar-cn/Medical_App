package com.medapp.order.service;

import com.medapp.audit.service.AuditService;
import com.medapp.cart.entity.CartItem;
import com.medapp.cart.service.CartService;
import com.medapp.common.dto.PageResponse;
import com.medapp.common.event.OrderStatusChangedEvent;
import com.medapp.common.exception.NotFoundException;
import com.medapp.inventory.dto.StockReservationRequest;
import com.medapp.inventory.service.InventoryService;
import com.medapp.order.domain.OrderDomain;
import com.medapp.order.domain.OrderStateMachine;
import com.medapp.order.dto.CreateOrderRequest;
import com.medapp.order.dto.OrderDto;
import com.medapp.order.dto.OrderStatusTransitionRequest;
import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderItem;
import com.medapp.order.entity.OrderStatus;
import com.medapp.order.exception.OrderException;
import com.medapp.order.mapper.OrderMapper;
import com.medapp.user.entity.User;
import com.medapp.user.entity.UserAddress;
import com.medapp.user.repository.UserAddressRepository;
import com.medapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderDomain orderDomain;
    private final OrderStateMachine orderStateMachine;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final InventoryService inventoryService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderDomain orderDomain,
                        OrderStateMachine orderStateMachine,
                        CartService cartService,
                        UserRepository userRepository,
                        UserAddressRepository userAddressRepository,
                        InventoryService inventoryService,
                        AuditService auditService,
                        ApplicationEventPublisher eventPublisher) {
        this.orderDomain = orderDomain;
        this.orderStateMachine = orderStateMachine;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.inventoryService = inventoryService;
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrderDto create(UUID userId, CreateOrderRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserAddress address = userAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(request.addressId(), userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        List<CartItem> cartItems = cartService.getActiveCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new OrderException("Cannot create order with empty cart", HttpStatus.BAD_REQUEST);
        }

        UUID pharmacyId = cartItems.get(0).getPharmacy().getId();
        if (cartItems.stream().anyMatch(item -> !item.getPharmacy().getId().equals(pharmacyId))) {
            throw new OrderException("Cart must contain items from one pharmacy per order", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setUser(user);
        order.setPharmacy(cartItems.get(0).getPharmacy());
        order.setStatus(OrderStatus.CREATED);
        order.setDeliveryAddressLine1(address.getLine1());
        order.setDeliveryAddressLine2(address.getLine2());
        order.setDeliveryCity(address.getCity());
        order.setDeliveryState(address.getState());
        order.setDeliveryPincode(address.getPincode());
        order.setDeliveryLatitude(address.getLatitude().doubleValue());
        order.setDeliveryLongitude(address.getLongitude().doubleValue());

        BigDecimal total = cartItems.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order savedOrder = orderDomain.saveOrder(order);

        List<StockReservationRequest> reservations = new ArrayList<>();
        boolean prescriptionRequired = false;
        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setMedicine(cartItem.getMedicine());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setGstPercentage(cartItem.getMedicine().getGstPercentage());
            orderDomain.saveOrderItem(item);

            if (cartItem.getMedicine().isPrescriptionRequired()) {
                prescriptionRequired = true;
                savedOrder.setPrescription(cartItem.getPrescription());
            }
            reservations.add(new StockReservationRequest(cartItem.getMedicine().getId(), cartItem.getQuantity()));
        }

        inventoryService.reserveStock(userId, savedOrder.getId(), pharmacyId, reservations);

        transitionInternal(savedOrder, prescriptionRequired ? OrderStatus.PRESCRIPTION_PENDING : OrderStatus.PAYMENT_PENDING,
                userId, "ORDER_CREATE", "Order created and stock reserved");

        cartService.clear(userId);
        auditService.record(userId, "ORDER_CREATED", "ORDER", savedOrder.getId(), "Order initiated from cart");
        return get(savedOrder.getId());
    }

    @Transactional
    public OrderDto transition(UUID actorUserId, UUID orderId, OrderStatusTransitionRequest request, String source) {
        Order order = orderDomain.getOrder(orderId);
        transitionInternal(order, request.status(), actorUserId, source, request.remarks());
        return get(orderId);
    }

    @Transactional
    public void transitionBySystem(UUID actorUserId, UUID orderId, OrderStatus target, String source, String remarks) {
        Order order = orderDomain.getOrder(orderId);
        transitionInternal(order, target, actorUserId, source, remarks);
    }

    private void transitionInternal(Order order,
                                    OrderStatus target,
                                    UUID actorUserId,
                                    String source,
                                    String remarks) {
        OrderStatus from = order.getStatus();
        orderStateMachine.validateTransition(from, target);

        if (target == OrderStatus.PAYMENT_SUCCESS) {
            inventoryService.confirmReservation(actorUserId, order.getId());
        }

        if (target == OrderStatus.CANCELLED || target == OrderStatus.REJECTED) {
            if (from == OrderStatus.PAYMENT_PENDING || from == OrderStatus.CREATED || from == OrderStatus.PRESCRIPTION_PENDING
                    || from == OrderStatus.PRESCRIPTION_APPROVED) {
                inventoryService.releaseReservation(actorUserId, order.getId());
            }
        }

        if (target == OrderStatus.RIDER_ASSIGNED && order.getDeliveryOtpHash() == null) {
            int otp = new SecureRandom().nextInt(9000) + 1000;
            order.setDeliveryOtpHash(Integer.toString(otp));
        }

        order.setStatus(target);
        orderDomain.saveOrder(order);
        orderDomain.logEvent(order, from, target, actorUserId, source, remarks);
        auditService.record(actorUserId, "ORDER_STATE_CHANGED", "ORDER", order.getId(), from + " -> " + target);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(order.getId(), order.getUser().getId(), from, target, source));
    }

    public OrderDto get(UUID orderId) {
        Order order = orderDomain.getOrder(orderId);
        return OrderMapper.toDto(order, orderDomain.orderItems(orderId), orderDomain.orderEvents(orderId));
    }

    public PageResponse<OrderDto> history(UUID userId, int page, int size) {
        Page<OrderDto> orderDtos = orderDomain.userOrders(userId, PageRequest.of(page, size))
                .map(order -> OrderMapper.toDto(
                        order,
                        orderDomain.orderItems(order.getId()),
                        orderDomain.orderEvents(order.getId())
                ));

        return new PageResponse<>(
                orderDtos.getContent(),
                orderDtos.getNumber(),
                orderDtos.getSize(),
                orderDtos.getTotalElements(),
                orderDtos.getTotalPages(),
                orderDtos.isLast()
        );
    }

    @Transactional
    public void assignRider(UUID actorId, UUID orderId, UUID riderId) {
        Order order = orderDomain.getOrder(orderId);
        order.setRiderId(riderId);
        transitionInternal(order, OrderStatus.RIDER_ASSIGNED, actorId, "DELIVERY", "Rider assigned");
    }

    public Order getOrderEntity(UUID orderId) {
        return orderDomain.getOrder(orderId);
    }

    @Transactional
    public void verifyDeliveryOtp(UUID riderId, UUID orderId, String otp) {
        Order order = orderDomain.getOrder(orderId);
        if (order.getRiderId() == null || !order.getRiderId().equals(riderId)) {
            throw new OrderException("Rider not assigned to this order", HttpStatus.FORBIDDEN);
        }
        if (!otp.equals(order.getDeliveryOtpHash())) {
            throw new OrderException("Invalid delivery OTP", HttpStatus.BAD_REQUEST);
        }
        transitionInternal(order, OrderStatus.DELIVERED, riderId, "DELIVERY", "Delivery OTP verified");
    }
}
