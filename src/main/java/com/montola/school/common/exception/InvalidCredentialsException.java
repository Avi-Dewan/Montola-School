package com.montola.school.common.exception;

public class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException() {
        super("auth.invalid.credentials");
    }
}