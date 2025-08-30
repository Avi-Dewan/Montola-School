package com.montola.school.common.exception;

public class UserNotFoundException extends AuthenticationException {

    public UserNotFoundException() {
        super("auth.user.notfound");
    }
}