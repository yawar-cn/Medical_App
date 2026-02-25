package com.medapp.notification.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotificationMessageValidator implements ConstraintValidator<ValidNotificationMessage, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.length() <= 500;
    }
}
