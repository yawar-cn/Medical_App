package com.medapp.medicine.service;

import com.medapp.audit.service.AuditService;
import com.medapp.common.dto.PageResponse;
import com.medapp.medicine.domain.MedicineDomain;
import com.medapp.medicine.dto.MedicineCreateRequest;
import com.medapp.medicine.dto.MedicineDto;
import com.medapp.medicine.dto.MedicineUpdateRequest;
import com.medapp.medicine.entity.Medicine;
import com.medapp.medicine.mapper.MedicineMapper;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {

    private final MedicineDomain medicineDomain;
    private final AuditService auditService;

    public MedicineService(MedicineDomain medicineDomain, AuditService auditService) {
        this.medicineDomain = medicineDomain;
        this.auditService = auditService;
    }

    @Transactional
    @CacheEvict(value = "medicineSearch", allEntries = true)
    public MedicineDto create(UUID adminId, MedicineCreateRequest request) {
        Medicine medicine = medicineDomain.save(MedicineMapper.toEntity(request));
        auditService.record(adminId, "INVENTORY_MEDICINE_CREATED", "MEDICINE", medicine.getId(), "Medicine added to master catalog");
        return MedicineMapper.toDto(medicine);
    }

    @Transactional
    @CacheEvict(value = "medicineSearch", allEntries = true)
    public MedicineDto update(UUID adminId, UUID medicineId, MedicineUpdateRequest request) {
        Medicine medicine = medicineDomain.getById(medicineId);
        MedicineMapper.updateEntity(medicine, request);
        Medicine saved = medicineDomain.save(medicine);
        auditService.record(adminId, "INVENTORY_MEDICINE_UPDATED", "MEDICINE", saved.getId(), "Medicine updated");
        return MedicineMapper.toDto(saved);
    }

    @Cacheable(value = "medicineSearch", key = "T(java.util.Objects).hash(#query,#page,#size)")
    public PageResponse<MedicineDto> search(String query, int page, int size) {
        Page<MedicineDto> result = medicineDomain.search(query, PageRequest.of(page, size)).map(MedicineMapper::toDto);
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    public MedicineDto get(UUID medicineId) {
        return MedicineMapper.toDto(medicineDomain.getById(medicineId));
    }
}
