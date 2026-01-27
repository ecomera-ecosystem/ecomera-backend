package com.youssef.ecomera.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }
}
