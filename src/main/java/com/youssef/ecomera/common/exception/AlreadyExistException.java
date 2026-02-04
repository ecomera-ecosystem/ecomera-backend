package com.youssef.ecomera.common.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends ApiException {

    // Default to 409 Conflict for all duplicate errors
    public AlreadyExistException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public AlreadyExistException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }

    // Convenience constructors for common cases
    public AlreadyExistException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT
        );
    }

    public AlreadyExistException(String resourceName, String fieldName, Object fieldValue, Throwable cause) {
        super(
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT,
                cause
        );
    }
}