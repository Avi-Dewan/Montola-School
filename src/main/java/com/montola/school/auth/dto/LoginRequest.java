package com.montola.school.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{5,}$",
                message = "Password must be at least 5 characters long and include at least one letter and one number"
        )
        String password
) {}
