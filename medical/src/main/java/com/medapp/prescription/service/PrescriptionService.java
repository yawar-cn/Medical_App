package com.medapp.prescription.service;

import com.medapp.audit.service.AuditService;
import com.medapp.common.dto.PageResponse;
import com.medapp.common.exception.NotFoundException;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.repository.PharmacyRepository;
import com.medapp.prescription.domain.PrescriptionDomain;
import com.medapp.prescription.dto.PrescriptionDto;
import com.medapp.prescription.dto.PrescriptionReviewRequest;
import com.medapp.prescription.dto.PrescriptionUploadRequest;
import com.medapp.prescription.entity.Prescription;
import com.medapp.prescription.entity.PrescriptionStatus;
import com.medapp.prescription.exception.PrescriptionException;
import com.medapp.prescription.mapper.PrescriptionMapper;
import com.medapp.user.entity.User;
import com.medapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService {

    private final PrescriptionDomain prescriptionDomain;
    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final AuditService auditService;

    public PrescriptionService(PrescriptionDomain prescriptionDomain,
                               UserRepository userRepository,
                               PharmacyRepository pharmacyRepository,
                               AuditService auditService) {
        this.prescriptionDomain = prescriptionDomain;
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.auditService = auditService;
    }

    @Transactional
    public PrescriptionDto upload(UUID userId, PrescriptionUploadRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Pharmacy pharmacy = request.pharmacyId() == null ? null : pharmacyRepository.findByIdAndDeletedAtIsNull(request.pharmacyId())
                .orElseThrow(() -> new NotFoundException("Pharmacy not found"));

        Prescription saved = prescriptionDomain.save(PrescriptionMapper.toEntity(request, user, pharmacy));
        auditService.record(userId, "PRESCRIPTION_UPLOADED", "PRESCRIPTION", saved.getId(), "Prescription uploaded by user");
        return PrescriptionMapper.toDto(saved);
    }

    @Transactional
    public PrescriptionDto review(UUID reviewerId, UUID prescriptionId, PrescriptionReviewRequest request) {
        Prescription prescription = prescriptionDomain.get(prescriptionId);
        if (prescription.getStatus() != PrescriptionStatus.PENDING) {
            throw new PrescriptionException("Prescription already reviewed", HttpStatus.CONFLICT);
        }

        prescription.setStatus(request.approved() ? PrescriptionStatus.APPROVED : PrescriptionStatus.REJECTED);
        prescription.setReviewerUserId(reviewerId);
        prescription.setReviewedAt(Instant.now());
        prescription.setReviewNotes(request.notes());

        Prescription saved = prescriptionDomain.save(prescription);
        auditService.record(
                reviewerId,
                request.approved() ? "PRESCRIPTION_APPROVED" : "PRESCRIPTION_REJECTED",
                "PRESCRIPTION",
                prescriptionId,
                request.notes()
        );
        return PrescriptionMapper.toDto(saved);
    }

    public PrescriptionDto get(UUID prescriptionId) {
        return PrescriptionMapper.toDto(prescriptionDomain.get(prescriptionId));
    }

    public PageResponse<PrescriptionDto> myPrescriptions(UUID userId, int page, int size) {
        Page<PrescriptionDto> result = prescriptionDomain.listByUser(userId, PageRequest.of(page, size)).map(PrescriptionMapper::toDto);
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    public void assertApproved(UUID prescriptionId, UUID userId) {
        if (!prescriptionDomain.isApprovedForUser(prescriptionId, userId)) {
            throw new PrescriptionException("Approved prescription required", HttpStatus.BAD_REQUEST);
        }
    }
}
