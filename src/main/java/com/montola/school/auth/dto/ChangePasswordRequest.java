package com.montola.school.auth.dto;

import lombok.Data;

/**
 * @author avidewan
 * @date 9/10/25
 */
@Data
public class ChangePasswordRequest {

    private String oldPassword;

    private String newPassword;
}