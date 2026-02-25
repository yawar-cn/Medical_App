package com.medapp.medicine.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class GstPercentageValidator implements ConstraintValidator<ValidGstPercentage, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(BigDecimal.valueOf(28)) <= 0;
    }
}
