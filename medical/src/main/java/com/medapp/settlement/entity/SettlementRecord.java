package com.medapp.settlement.entity;

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
import java.util.UUID;

@Entity
@Table(name = "settlements")
public class SettlementRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private UUID pharmacyId;

    @Column
    private UUID riderId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal commissionAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pharmacyPayout;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal riderPayout;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    @Column
    private Instant settledAt;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public UUID getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(UUID pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public UUID getRiderId() {
        return riderId;
    }

    public void setRiderId(UUID riderId) {
        this.riderId = riderId;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getPharmacyPayout() {
        return pharmacyPayout;
    }

    public void setPharmacyPayout(BigDecimal pharmacyPayout) {
        this.pharmacyPayout = pharmacyPayout;
    }

    public BigDecimal getRiderPayout() {
        return riderPayout;
    }

    public void setRiderPayout(BigDecimal riderPayout) {
        this.riderPayout = riderPayout;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementStatus status) {
        this.status = status;
    }

    public Instant getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(Instant settledAt) {
        this.settledAt = settledAt;
    }
}
