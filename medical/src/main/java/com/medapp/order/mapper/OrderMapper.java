package com.medapp.order.mapper;

import com.medapp.order.dto.OrderDto;
import com.medapp.order.dto.OrderEventDto;
import com.medapp.order.dto.OrderItemDto;
import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderEvent;
import com.medapp.order.entity.OrderItem;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderItemDto toItemDto(OrderItem item) {
        return new OrderItemDto(
                item.getMedicine().getId(),
                item.getMedicine().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getGstPercentage()
        );
    }

    public static OrderEventDto toEventDto(OrderEvent event) {
        return new OrderEventDto(
                event.getId(),
                event.getFromStatus(),
                event.getToStatus(),
                event.getActorUserId(),
                event.getSource(),
                event.getEventTime(),
                event.getRemarks()
        );
    }

    public static OrderDto toDto(Order order, List<OrderItem> items, List<OrderEvent> events) {
        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getPharmacy().getId(),
                order.getPrescription() == null ? null : order.getPrescription().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getRiderId(),
                order.getCreatedAt(),
                items.stream().map(OrderMapper::toItemDto).toList(),
                events.stream().map(OrderMapper::toEventDto).toList()
        );
    }
}
