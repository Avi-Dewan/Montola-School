package com.montola.school.auth.dto;

import java.util.Set;

/**
 * @author avidewan
 * @date 8/29/25
 */
public record AuthResponse(

        String accessToken,

        String refreshToken,

        String email,

        String fullName,

        Set<String> roles
) {}
