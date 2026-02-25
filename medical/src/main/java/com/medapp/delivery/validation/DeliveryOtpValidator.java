package com.medapp.delivery.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DeliveryOtpValidator implements ConstraintValidator<ValidDeliveryOtp, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("^[0-9]{4}$");
    }
}
