package com.montola.school.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author avidewan
 * @date 9/1/25
 */
@Data
public class ActivationRequest {

    @Email
    private String email;

    @NotBlank
    private String token;
}