package com.medapp.pharmacy.domain;

import com.medapp.common.exception.NotFoundException;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.entity.PharmacyStatus;
import com.medapp.pharmacy.repository.PharmacyRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PharmacyDomain {

    private final PharmacyRepository pharmacyRepository;

    public PharmacyDomain(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    public Pharmacy save(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }

    public Pharmacy getById(UUID id) {
        return pharmacyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy not found"));
    }

    public Pharmacy getByOwner(UUID ownerId) {
        return pharmacyRepository.findByOwnerUserIdAndDeletedAtIsNull(ownerId)
                .orElseThrow(() -> new NotFoundException("Pharmacy profile not found"));
    }

    public boolean licenseExists(String license) {
        return pharmacyRepository.existsByLicenseNumber(license);
    }

    public Page<Pharmacy> listApproved(Pageable pageable) {
        return pharmacyRepository.findByStatusAndDeletedAtIsNull(PharmacyStatus.APPROVED, pageable);
    }
}
