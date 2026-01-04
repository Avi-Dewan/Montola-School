package com.montola.school.common.security;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author avidewan
 * @date 9/2/25
 */
@Component
public class SecurityProperties {

    public List<String> getWhiteList() {
        return List.of(
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/activate",
                "/api/auth/resend-activation",
                "/api/auth/forgot-password",
                "/api/auth/reset-password",
                "/api/v1/classes",
                "/api/v1/featured-chapters",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
        );
    }

    public List<String> getCorsAllowedOrigins() {
        return List.of(
                "http://localhost:3000",
                "https://yourfrontend.com"
        );
    }
}