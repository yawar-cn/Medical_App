package com.medapp.audit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class AuditActionValidator implements ConstraintValidator<ValidAuditAction, String> {

    private static final Set<String> ALLOWED_PREFIXES = Set.of(
            "PRESCRIPTION_",
            "ORDER_",
            "REFUND_",
            "INVENTORY_",
            "PAYMENT_",
            "USER_",
            "PHARMACY_"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return ALLOWED_PREFIXES.stream().anyMatch(value::startsWith);
    }
}
