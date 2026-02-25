package com.medapp.order.domain;

import com.medapp.order.entity.OrderStatus;
import com.medapp.order.exception.OrderException;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OrderStateMachine {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.ofEntries(
            Map.entry(OrderStatus.CREATED, Set.of(OrderStatus.PRESCRIPTION_PENDING, OrderStatus.PAYMENT_PENDING, OrderStatus.CANCELLED)),
            Map.entry(OrderStatus.PRESCRIPTION_PENDING, Set.of(OrderStatus.PRESCRIPTION_APPROVED, OrderStatus.REJECTED, OrderStatus.CANCELLED)),
            Map.entry(OrderStatus.PRESCRIPTION_APPROVED, Set.of(OrderStatus.PAYMENT_PENDING, OrderStatus.CANCELLED)),
            Map.entry(OrderStatus.PAYMENT_PENDING, Set.of(OrderStatus.PAYMENT_SUCCESS, OrderStatus.CANCELLED, OrderStatus.REJECTED)),
            Map.entry(OrderStatus.PAYMENT_SUCCESS, Set.of(OrderStatus.PHARMACY_ACCEPTED, OrderStatus.REFUNDED)),
            Map.entry(OrderStatus.PHARMACY_ACCEPTED, Set.of(OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED)),
            Map.entry(OrderStatus.READY_FOR_PICKUP, Set.of(OrderStatus.RIDER_ASSIGNED)),
            Map.entry(OrderStatus.RIDER_ASSIGNED, Set.of(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.CANCELLED)),
            Map.entry(OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED)),
            Map.entry(OrderStatus.DELIVERED, Set.of(OrderStatus.REFUNDED)),
            Map.entry(OrderStatus.CANCELLED, Set.of(OrderStatus.REFUNDED)),
            Map.entry(OrderStatus.REJECTED, Set.of(OrderStatus.REFUNDED)),
            Map.entry(OrderStatus.REFUNDED, Set.of())
    );

    public void validateTransition(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new OrderException("Invalid order transition: " + from + " -> " + to, HttpStatus.BAD_REQUEST);
        }
    }
}
