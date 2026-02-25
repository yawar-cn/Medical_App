package com.medapp.inventory.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ExpiryDateValidator implements ConstraintValidator<ValidExpiryDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(LocalDate.now());
    }
}
