package com.medapp.prescription.repository;

import com.medapp.prescription.entity.Prescription;
import com.medapp.prescription.entity.PrescriptionStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Page<Prescription> findByUserId(UUID userId, Pageable pageable);

    Optional<Prescription> findById(UUID id);

    boolean existsByIdAndUserIdAndStatus(UUID id, UUID userId, PrescriptionStatus status);
}
