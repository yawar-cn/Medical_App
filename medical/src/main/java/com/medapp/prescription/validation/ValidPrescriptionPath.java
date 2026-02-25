package com.medapp.prescription.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PrescriptionPathValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrescriptionPath {
    String message() default "Prescription path must be a non-executable storage path";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
