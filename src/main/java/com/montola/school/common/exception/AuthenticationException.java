package com.montola.school.common.exception;


import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final String messageKey;

    public AuthenticationException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }
}