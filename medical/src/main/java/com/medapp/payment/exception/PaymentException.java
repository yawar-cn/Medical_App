package com.medapp.payment.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class PaymentException extends ApiException {
    public PaymentException(String message, HttpStatus status) {
        super(status, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
