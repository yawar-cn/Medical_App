package com.medapp.prescription.dto;

import com.medapp.prescription.validation.ValidPrescriptionPath;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record PrescriptionUploadRequest(
        UUID pharmacyId,
        @NotBlank @ValidPrescriptionPath String filePath
) {
}
