package com.medapp.prescription.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class PrescriptionException extends ApiException {
    public PrescriptionException(String message, HttpStatus status) {
        super(status, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
