package com.order.management.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ControllerAdvice;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Centralizes exception handling. Every caught exception is logged, then translated
 * into a consistent ErrorResponse payload.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("ResourceNotFoundException: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.builder()
            .errorCode("NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        
        log.error("DataIntegrityViolationException: {}", ex.getRootCause() != null 
            ? ex.getRootCause().getMessage() 
            : ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .errorCode("DUPLICATE_RESOURCE")
            .message("A database constraint was violated. Please ensure unique values or valid references.")
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

   
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String combinedErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", combinedErrors);
        ErrorResponse body = ErrorResponse.builder()
            .errorCode("VALIDATION_FAILED")
            .message(combinedErrors)
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception: ", ex);
        ErrorResponse body = ErrorResponse.builder()
            .errorCode("INTERNAL_ERROR")
            .message("An unexpected error occurred. Please try again later.")
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
