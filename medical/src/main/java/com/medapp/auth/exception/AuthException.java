package com.medapp.auth.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuthException extends ApiException {
    public AuthException(String message, HttpStatus status) {
        super(status, ErrorCode.UNAUTHORIZED, message);
    }
}
