package com.medapp.user.validation;

import com.medapp.user.dto.AddressRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GeoCoordinateValidator implements ConstraintValidator<ValidGeoCoordinate, AddressRequest> {

    @Override
    public boolean isValid(AddressRequest value, ConstraintValidatorContext context) {
        if (value == null || value.latitude() == null || value.longitude() == null) {
            return true;
        }
        return value.latitude().doubleValue() != 0.0 || value.longitude().doubleValue() != 0.0;
    }
}
