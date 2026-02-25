package com.medapp.pharmacy.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class PharmacyException extends ApiException {
    public PharmacyException(String message, HttpStatus status) {
        super(status, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
