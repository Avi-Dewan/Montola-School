package com.montola.school.common.exception;

public class AccessDeniedCustomException extends RuntimeException {

    public AccessDeniedCustomException() {
        super("auth.access.denied");
    }
}