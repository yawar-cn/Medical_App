package com.medapp.notification.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotificationException extends ApiException {
    public NotificationException(String message) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
