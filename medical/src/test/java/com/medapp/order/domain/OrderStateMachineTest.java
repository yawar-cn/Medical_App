package com.medapp.order.domain;

import com.medapp.order.entity.OrderStatus;
import com.medapp.order.exception.OrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStateMachineTest {

    private OrderStateMachine orderStateMachine;

    @BeforeEach
    void setUp() {
        orderStateMachine = new OrderStateMachine();
    }

    @Test
    void shouldAllowValidTransitions() {
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.CREATED, OrderStatus.PRESCRIPTION_PENDING));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.PRESCRIPTION_PENDING, OrderStatus.PRESCRIPTION_APPROVED));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.PRESCRIPTION_APPROVED, OrderStatus.PAYMENT_PENDING));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.PAYMENT_PENDING, OrderStatus.PAYMENT_SUCCESS));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.PAYMENT_SUCCESS, OrderStatus.PHARMACY_ACCEPTED));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.PHARMACY_ACCEPTED, OrderStatus.READY_FOR_PICKUP));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.READY_FOR_PICKUP, OrderStatus.RIDER_ASSIGNED));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.RIDER_ASSIGNED, OrderStatus.OUT_FOR_DELIVERY));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED));
        assertDoesNotThrow(() -> orderStateMachine.validateTransition(OrderStatus.DELIVERED, OrderStatus.REFUNDED));
    }

    @Test
    void shouldRejectInvalidTransitions() {
        assertThrows(OrderException.class, () -> orderStateMachine.validateTransition(OrderStatus.CREATED, OrderStatus.DELIVERED));
        assertThrows(OrderException.class, () -> orderStateMachine.validateTransition(OrderStatus.PAYMENT_PENDING, OrderStatus.PHARMACY_ACCEPTED));
        assertThrows(OrderException.class, () -> orderStateMachine.validateTransition(OrderStatus.PRESCRIPTION_PENDING, OrderStatus.PAYMENT_SUCCESS));
        assertThrows(OrderException.class, () -> orderStateMachine.validateTransition(OrderStatus.REFUNDED, OrderStatus.PAYMENT_SUCCESS));
    }
}
