package com.medapp.medicine.mapper;

import com.medapp.medicine.dto.MedicineCreateRequest;
import com.medapp.medicine.dto.MedicineDto;
import com.medapp.medicine.dto.MedicineUpdateRequest;
import com.medapp.medicine.entity.Medicine;

public final class MedicineMapper {

    private MedicineMapper() {
    }

    public static Medicine toEntity(MedicineCreateRequest request) {
        Medicine medicine = new Medicine();
        medicine.setName(request.name());
        medicine.setGenericName(request.genericName());
        medicine.setManufacturer(request.manufacturer());
        medicine.setCategory(request.category());
        medicine.setGstPercentage(request.gstPercentage());
        medicine.setPrescriptionRequired(request.prescriptionRequired());
        medicine.setMrp(request.mrp());
        medicine.setActive(true);
        return medicine;
    }

    public static void updateEntity(Medicine medicine, MedicineUpdateRequest request) {
        medicine.setName(request.name());
        medicine.setGenericName(request.genericName());
        medicine.setManufacturer(request.manufacturer());
        medicine.setCategory(request.category());
        medicine.setGstPercentage(request.gstPercentage());
        medicine.setPrescriptionRequired(request.prescriptionRequired());
        medicine.setMrp(request.mrp());
        medicine.setActive(request.active());
    }

    public static MedicineDto toDto(Medicine medicine) {
        return new MedicineDto(
                medicine.getId(),
                medicine.getName(),
                medicine.getGenericName(),
                medicine.getManufacturer(),
                medicine.getCategory(),
                medicine.getGstPercentage(),
                medicine.isPrescriptionRequired(),
                medicine.getMrp(),
                medicine.isActive()
        );
    }
}
