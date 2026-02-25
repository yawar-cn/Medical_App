package com.medapp.prescription.mapper;

import com.medapp.pharmacy.entity.Pharmacy;
import com.medapp.prescription.dto.PrescriptionDto;
import com.medapp.prescription.dto.PrescriptionUploadRequest;
import com.medapp.prescription.entity.Prescription;
import com.medapp.prescription.entity.PrescriptionStatus;
import com.medapp.user.entity.User;

public final class PrescriptionMapper {

    private PrescriptionMapper() {
    }

    public static Prescription toEntity(PrescriptionUploadRequest request, User user, Pharmacy pharmacy) {
        Prescription prescription = new Prescription();
        prescription.setUser(user);
        prescription.setPharmacy(pharmacy);
        prescription.setFilePath(request.filePath());
        prescription.setStatus(PrescriptionStatus.PENDING);
        return prescription;
    }

    public static PrescriptionDto toDto(Prescription prescription) {
        return new PrescriptionDto(
                prescription.getId(),
                prescription.getUser().getId(),
                prescription.getPharmacy() == null ? null : prescription.getPharmacy().getId(),
                prescription.getFilePath(),
                prescription.getStatus(),
                prescription.getReviewerUserId(),
                prescription.getReviewedAt(),
                prescription.getReviewNotes()
        );
    }
}
