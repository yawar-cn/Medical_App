package com.medapp.delivery.entity;

import com.medapp.common.entity.BaseEntity;
import com.medapp.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "delivery_assignments")
public class DeliveryAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rider_id", nullable = false)
    private RiderProfile rider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal earningAmount;

    @Column(nullable = false)
    private Instant assignedAt;

    @Column
    private Instant deliveredAt;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public RiderProfile getRider() {
        return rider;
    }

    public void setRider(RiderProfile rider) {
        this.rider = rider;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public BigDecimal getEarningAmount() {
        return earningAmount;
    }

    public void setEarningAmount(BigDecimal earningAmount) {
        this.earningAmount = earningAmount;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
