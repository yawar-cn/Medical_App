package com.medapp.payment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WebhookSignatureValidator implements ConstraintValidator<ValidWebhookSignature, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.isBlank() && value.length() >= 16;
    }
}
