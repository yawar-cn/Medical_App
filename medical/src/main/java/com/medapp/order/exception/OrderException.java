package com.medapp.order.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class OrderException extends ApiException {
    public OrderException(String message, HttpStatus status) {
        super(status, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
