package com.montola.school.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author avidewan
 * @date 8/29/25
 */
public record LoginRequest(

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 4)
        String password
) {}
