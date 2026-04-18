package com.montola.school.common.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author avidewan
 * @date 9/2/25
 */
@Getter
@Component
public class SecurityProperties {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public record WhitelistRule(String pattern, String method) {}

    private final List<WhitelistRule> whiteList = List.of(
            new WhitelistRule("/internal/health", "GET"),
            new WhitelistRule("/favicon.ico", "GET"),
            new WhitelistRule("/error", null),
            new WhitelistRule("/api/auth/login", "POST"),
            new WhitelistRule("/api/auth/refresh-token", "POST"),
            new WhitelistRule("/api/auth/register", "POST"),
            new WhitelistRule("/api/auth/activate", "POST"),
            new WhitelistRule("/api/auth/resend-activation", "POST"),
            new WhitelistRule("/api/auth/forgot-password", "POST"),
            new WhitelistRule("/api/auth/reset-password", "POST"),
            new WhitelistRule("/api/v1/classes", "GET"),
            new WhitelistRule("/api/v1/classes/*/public-structure", "GET"),
            new WhitelistRule("/api/v1/chapters/*/cover-image", "GET"),
            new WhitelistRule("/api/v1/chapters/public/free", "GET"),
            new WhitelistRule("/api/v1/chapters/*/public", "GET"),
            new WhitelistRule("/api/v1/featured-chapters", "GET"),
            new WhitelistRule("/v3/api-docs/**", "GET"),
            new WhitelistRule("/swagger-ui/**", "GET"),
            new WhitelistRule("/swagger-ui.html", "GET")
    );

    public List<String> getCorsAllowedOrigins() {
        return List.of(frontendUrl);
    }
}
