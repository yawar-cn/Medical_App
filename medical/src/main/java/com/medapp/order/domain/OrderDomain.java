package com.medapp.order.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderEvent;
import com.medapp.order.entity.OrderItem;
import com.medapp.order.repository.OrderEventRepository;
import com.medapp.order.repository.OrderItemRepository;
import com.medapp.order.repository.OrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class OrderDomain {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventRepository orderEventRepository;

    public OrderDomain(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       OrderEventRepository orderEventRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderEventRepository = orderEventRepository;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public OrderItem saveOrderItem(OrderItem item) {
        return orderItemRepository.save(item);
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public Page<Order> userOrders(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public List<OrderItem> orderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderEvent> orderEvents(UUID orderId) {
        return orderEventRepository.findByOrderIdOrderByEventTimeAsc(orderId);
    }

    public void logEvent(Order order,
                         com.medapp.order.entity.OrderStatus from,
                         com.medapp.order.entity.OrderStatus to,
                         UUID actorUserId,
                         String source,
                         String remarks) {
        OrderEvent event = new OrderEvent();
        event.setOrder(order);
        event.setFromStatus(from);
        event.setToStatus(to);
        event.setActorUserId(actorUserId);
        event.setSource(source);
        event.setEventTime(Instant.now());
        event.setRemarks(remarks == null ? "" : remarks);
        orderEventRepository.save(event);
    }
}
