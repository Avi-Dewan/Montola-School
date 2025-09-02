package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/27/25
 */
public class UserNotFoundException extends AuthenticationException {

    public UserNotFoundException() {
        super("auth.user.notfound");
    }
}