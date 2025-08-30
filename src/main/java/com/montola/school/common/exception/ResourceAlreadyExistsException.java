package com.montola.school.common.exception;

import lombok.Getter;

/**
 * @author avidewan
 * @date 8/30/25
 */
@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    private final String messageKey;

    public ResourceAlreadyExistsException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

}
