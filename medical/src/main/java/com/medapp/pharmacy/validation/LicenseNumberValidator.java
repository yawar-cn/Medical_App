package com.medapp.pharmacy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LicenseNumberValidator implements ConstraintValidator<ValidLicenseNumber, String> {

    private static final String LICENSE_REGEX = "^[A-Z0-9-]{8,20}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(LICENSE_REGEX);
    }
}
