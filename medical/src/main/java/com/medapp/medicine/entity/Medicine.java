package com.medapp.medicine.entity;

import com.medapp.common.entity.SoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "medicines")
public class Medicine extends SoftDeleteEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String genericName;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Column(nullable = false)
    private boolean prescriptionRequired;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(nullable = false)
    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    public BigDecimal getMrp() {
        return mrp;
    }

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
