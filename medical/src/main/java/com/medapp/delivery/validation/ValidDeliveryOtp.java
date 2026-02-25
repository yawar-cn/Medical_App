package com.medapp.delivery.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DeliveryOtpValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDeliveryOtp {
    String message() default "Delivery OTP must be 4 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
