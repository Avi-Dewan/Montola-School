package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/27/25
 */
public class TokenMissingException extends AuthenticationException {

    public TokenMissingException(String messageKey) {
        super(messageKey);
    }
}
