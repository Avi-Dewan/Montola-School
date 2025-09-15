package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/27/25
 */
public class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException() {
        super("auth.invalid.credentials");
    }
}