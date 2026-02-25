package com.medapp.user.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserException extends ApiException {
    public UserException(String message) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
