package com.medapp.cart.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CartQuantityValidator implements ConstraintValidator<ValidCartQuantity, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value >= 1 && value <= 20;
    }
}
