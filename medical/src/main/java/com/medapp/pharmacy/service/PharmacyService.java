package com.medapp.pharmacy.service;

import com.medapp.audit.service.AuditService;
import com.medapp.common.dto.PageResponse;
import com.medapp.common.exception.ConflictException;
import com.medapp.pharmacy.domain.PharmacyDomain;
import com.medapp.pharmacy.dto.PharmacyApprovalRequest;
import com.medapp.pharmacy.dto.PharmacyCreateRequest;
import com.medapp.pharmacy.dto.PharmacyDto;
import com.medapp.pharmacy.dto.PharmacyUpdateRequest;
import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.pharmacy.entity.PharmacyStatus;
import com.medapp.pharmacy.exception.PharmacyException;
import com.medapp.pharmacy.mapper.PharmacyMapper;
import com.medapp.user.entity.User;
import com.medapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PharmacyService {

    private final PharmacyDomain pharmacyDomain;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public PharmacyService(PharmacyDomain pharmacyDomain,
                           UserRepository userRepository,
                           AuditService auditService) {
        this.pharmacyDomain = pharmacyDomain;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public PharmacyDto create(UUID ownerId, PharmacyCreateRequest request) {
        if (pharmacyDomain.licenseExists(request.licenseNumber())) {
            throw new ConflictException("License number already registered");
        }
        User owner = userRepository.findByIdAndDeletedAtIsNull(ownerId)
                .orElseThrow(() -> new PharmacyException("Owner not found", HttpStatus.NOT_FOUND));
        Pharmacy pharmacy = pharmacyDomain.save(PharmacyMapper.toEntity(request, owner));
        auditService.record(ownerId, "PHARMACY_CREATED", "PHARMACY", pharmacy.getId(), "Pharmacy KYC submitted");
        return PharmacyMapper.toDto(pharmacy);
    }

    public PharmacyDto me(UUID ownerId) {
        return PharmacyMapper.toDto(pharmacyDomain.getByOwner(ownerId));
    }

    @Transactional
    public PharmacyDto update(UUID ownerId, PharmacyUpdateRequest request) {
        Pharmacy pharmacy = pharmacyDomain.getByOwner(ownerId);
        PharmacyMapper.updateEntity(pharmacy, request);
        pharmacy.setStatus(PharmacyStatus.PENDING);
        Pharmacy saved = pharmacyDomain.save(pharmacy);
        auditService.record(ownerId, "PHARMACY_UPDATED", "PHARMACY", saved.getId(), "Pharmacy profile updated");
        return PharmacyMapper.toDto(saved);
    }

    @Transactional
    public PharmacyDto review(UUID adminId, UUID pharmacyId, PharmacyApprovalRequest request) {
        Pharmacy pharmacy = pharmacyDomain.getById(pharmacyId);
        if (request.approved()) {
            pharmacy.setStatus(PharmacyStatus.APPROVED);
            pharmacy.setRejectionReason(null);
            auditService.record(adminId, "PHARMACY_APPROVED", "PHARMACY", pharmacyId, request.reason());
        } else {
            pharmacy.setStatus(PharmacyStatus.REJECTED);
            pharmacy.setRejectionReason(request.reason());
            auditService.record(adminId, "PHARMACY_REJECTED", "PHARMACY", pharmacyId, request.reason());
        }
        return PharmacyMapper.toDto(pharmacyDomain.save(pharmacy));
    }

    public PageResponse<PharmacyDto> approved(int page, int size) {
        Page<PharmacyDto> items = pharmacyDomain.listApproved(PageRequest.of(page, size)).map(PharmacyMapper::toDto);
        return new PageResponse<>(items.getContent(), items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
    }
}
