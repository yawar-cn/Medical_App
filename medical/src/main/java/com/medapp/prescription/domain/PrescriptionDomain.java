package com.medapp.prescription.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.prescription.entity.Prescription;
import com.medapp.prescription.entity.PrescriptionStatus;
import com.medapp.prescription.repository.PrescriptionRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionDomain {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionDomain(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public Prescription save(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    public Prescription get(UUID prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new NotFoundException("Prescription not found"));
    }

    public Page<Prescription> listByUser(UUID userId, Pageable pageable) {
        return prescriptionRepository.findByUserId(userId, pageable);
    }

    public boolean isApprovedForUser(UUID prescriptionId, UUID userId) {
        return prescriptionRepository.existsByIdAndUserIdAndStatus(prescriptionId, userId, PrescriptionStatus.APPROVED);
    }
}
