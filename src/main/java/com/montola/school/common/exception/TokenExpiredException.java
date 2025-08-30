package com.montola.school.common.exception;

public class TokenExpiredException extends AuthenticationException {

    public TokenExpiredException() {
        super("auth.token.expired");
    }
}
