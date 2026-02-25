package com.medapp.pharmacy.repository;

import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.entity.PharmacyStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, UUID> {
    Optional<Pharmacy> findByOwnerUserIdAndDeletedAtIsNull(UUID ownerUserId);

    Optional<Pharmacy> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByLicenseNumber(String licenseNumber);

    Page<Pharmacy> findByStatusAndDeletedAtIsNull(PharmacyStatus status, Pageable pageable);
}
