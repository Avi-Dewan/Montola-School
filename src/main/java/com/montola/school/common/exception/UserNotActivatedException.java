package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/30/25
 */
public class UserNotActivatedException extends RuntimeException {
    public UserNotActivatedException() {
        super("user.not.activated");
    }
}