package com.medapp.order.validation;

import com.medapp.order.entity.OrderStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrderStatusTransitionValidator implements ConstraintValidator<ValidOrderStatusTransition, OrderStatus> {

    @Override
    public boolean isValid(OrderStatus value, ConstraintValidatorContext context) {
        return value != null && value != OrderStatus.CREATED;
    }
}
