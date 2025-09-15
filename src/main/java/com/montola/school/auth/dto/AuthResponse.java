package com.montola.school.auth.dto;

/**
 * @author avidewan
 * @date 8/29/25
 */
public record AuthResponse(

        String token,

        String email
) {}
