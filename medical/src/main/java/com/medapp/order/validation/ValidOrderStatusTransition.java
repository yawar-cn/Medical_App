package com.medapp.order.validation;

import com.medapp.order.entity.OrderStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = OrderStatusTransitionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOrderStatusTransition {
    String message() default "Invalid target order status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
