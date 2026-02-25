package com.medapp.prescription.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PrescriptionPathValidator implements ConstraintValidator<ValidPrescriptionPath, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String lower = value.toLowerCase();
        return (lower.endsWith(".pdf") || lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
                && !lower.contains("..")
                && !lower.contains(".sh")
                && !lower.contains(".exe");
    }
}
