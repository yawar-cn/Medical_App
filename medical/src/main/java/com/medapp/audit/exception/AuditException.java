package com.medapp.audit.exception;

import com.medapp.common.exception.ApiException;
import com.medapp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuditException extends ApiException {
    public AuditException(String message) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
