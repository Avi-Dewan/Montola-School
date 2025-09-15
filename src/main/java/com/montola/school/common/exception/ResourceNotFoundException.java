package com.montola.school.common.exception;

import lombok.Getter;

/**
 * @author avidewan
 * @date 9/2/25
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String messageKey;

    public ResourceNotFoundException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }
}