package com.medapp.inventory.entity;

import com.medapp.common.entity.BaseEntity;
import com.medapp.medicine.entity.Medicine;
import com.medapp.pharmacy.entity.Pharmacy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_inventory_pharmacy_medicine_batch", columnNames = {
                "pharmacy_id", "medicine_id", "batch_number"
        }))
public class InventoryItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private String batchNumber;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private int quantityAvailable;

    @Column(nullable = false)
    private int quantityReserved;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Version
    private long version;

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityReserved() {
        return quantityReserved;
    }

    public void setQuantityReserved(int quantityReserved) {
        this.quantityReserved = quantityReserved;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public long getVersion() {
        return version;
    }
}
