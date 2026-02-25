package com.medapp.settlement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class CommissionValidator implements ConstraintValidator<ValidCommission, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(BigDecimal.valueOf(100)) <= 0;
    }
}
