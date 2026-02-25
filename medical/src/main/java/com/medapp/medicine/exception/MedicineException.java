package com.medapp.medicine.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class MedicineException extends ApiException {
    public MedicineException(String message) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
