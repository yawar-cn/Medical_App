package com.medapp.prescription.dto;

import com.medapp.prescription.entity.PrescriptionStatus;
import java.time.Instant;
import java.util.UUID;

public record PrescriptionDto(
        UUID id,
        UUID userId,
        UUID pharmacyId,
        String filePath,
        PrescriptionStatus status,
        UUID reviewerUserId,
        Instant reviewedAt,
        String reviewNotes
) {
}
