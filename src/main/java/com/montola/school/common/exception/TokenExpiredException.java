package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/27/25
 */
public class TokenExpiredException extends AuthenticationException {

    public TokenExpiredException() {
        super("auth.token.expired");
    }
}
