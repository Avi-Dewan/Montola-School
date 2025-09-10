package com.montola.school.auth.dto;

import lombok.Data;

/**
 * @author avidewan
 * @date 9/10/25
 */
@Data
public class ResetPasswordRequest {

    private String email;

    private String token;

    private String newPassword;
}