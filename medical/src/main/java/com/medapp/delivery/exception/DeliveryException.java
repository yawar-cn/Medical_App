package com.medapp.delivery.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class DeliveryException extends ApiException {
    public DeliveryException(String message, HttpStatus status) {
        super(status, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
