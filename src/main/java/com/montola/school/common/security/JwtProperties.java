package com.montola.school.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author avidewan
 * @date 8/29/25
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String secret,
        Integer expirationMinutes,
        Integer refreshExpirationDays
) {
}
