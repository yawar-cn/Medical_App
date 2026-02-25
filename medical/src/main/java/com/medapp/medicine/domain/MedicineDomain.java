package com.medapp.medicine.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.medicine.entity.Medicine;
import com.medapp.medicine.repository.MedicineRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MedicineDomain {

    private final MedicineRepository medicineRepository;

    public MedicineDomain(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public Medicine save(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public Medicine getById(UUID id) {
        return medicineRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Medicine not found"));
    }

    public Page<Medicine> search(String q, Pageable pageable) {
        return medicineRepository.searchActive(q, pageable);
    }
}
