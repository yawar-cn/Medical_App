package com.medapp.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(
                        Instant.now(),
                        ex.getStatus().value(),
                        ex.getErrorCode().name(),
                        ex.getMessage(),
                        List.of(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.VALIDATION_ERROR.name(),
                        "Validation failed",
                        details,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.VALIDATION_ERROR.name(),
                        "Validation failed",
                        details,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler({BadCredentialsException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleSecurity(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = ex instanceof BadCredentialsException ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        ErrorCode code = ex instanceof BadCredentialsException ? ErrorCode.UNAUTHORIZED : ErrorCode.FORBIDDEN;
        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        code.name(),
                        ex.getMessage(),
                        List.of(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.INTERNAL_ERROR.name(),
                        "Unexpected server error",
                        List.of(),
                        request.getRequestURI()
                ));
    }
}
