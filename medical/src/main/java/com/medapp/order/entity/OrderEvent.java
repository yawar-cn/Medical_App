package com.medapp.order.entity;

import com.medapp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_events")
public class OrderEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus toStatus;

    @Column(nullable = false)
    private UUID actorUserId;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private Instant eventTime;

    @Column(nullable = false)
    private String remarks;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(OrderStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public OrderStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(OrderStatus toStatus) {
        this.toStatus = toStatus;
    }

    public UUID getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(UUID actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
