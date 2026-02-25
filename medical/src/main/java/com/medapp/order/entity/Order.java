package com.medapp.order.entity;

import com.medapp.common.entity.BaseEntity;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.prescription.entity.Prescription;
import com.medapp.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String deliveryAddressLine1;

    @Column
    private String deliveryAddressLine2;

    @Column(nullable = false)
    private String deliveryCity;

    @Column(nullable = false)
    private String deliveryState;

    @Column(nullable = false)
    private String deliveryPincode;

    @Column(nullable = false)
    private double deliveryLatitude;

    @Column(nullable = false)
    private double deliveryLongitude;

    @Column
    private UUID riderId;

    @Column
    private String deliveryOtpHash;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryAddressLine1() {
        return deliveryAddressLine1;
    }

    public void setDeliveryAddressLine1(String deliveryAddressLine1) {
        this.deliveryAddressLine1 = deliveryAddressLine1;
    }

    public String getDeliveryAddressLine2() {
        return deliveryAddressLine2;
    }

    public void setDeliveryAddressLine2(String deliveryAddressLine2) {
        this.deliveryAddressLine2 = deliveryAddressLine2;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        this.deliveryState = deliveryState;
    }

    public String getDeliveryPincode() {
        return deliveryPincode;
    }

    public void setDeliveryPincode(String deliveryPincode) {
        this.deliveryPincode = deliveryPincode;
    }

    public double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public UUID getRiderId() {
        return riderId;
    }

    public void setRiderId(UUID riderId) {
        this.riderId = riderId;
    }

    public String getDeliveryOtpHash() {
        return deliveryOtpHash;
    }

    public void setDeliveryOtpHash(String deliveryOtpHash) {
        this.deliveryOtpHash = deliveryOtpHash;
    }
}
