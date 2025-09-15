package com.montola.school.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author avidewan
 * @date 9/2/25
 */
@Data
public class EmailRequest {

    @Email
    @NotBlank
    private String email;
}

