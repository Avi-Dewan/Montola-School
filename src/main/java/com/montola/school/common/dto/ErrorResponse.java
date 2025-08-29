package com.montola.school.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author avidewan
 * @date 8/29/25
 */
public record ErrorResponse(

        int status,

        String message,

        LocalDateTime timestamp,

        Map<String, String> fieldErrors
) {}
