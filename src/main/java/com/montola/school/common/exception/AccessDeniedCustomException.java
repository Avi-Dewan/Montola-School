package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/27/25
 */
public class AccessDeniedCustomException extends RuntimeException {

    public AccessDeniedCustomException() {
        super("auth.access.denied");
    }
}