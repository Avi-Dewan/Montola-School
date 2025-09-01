package com.montola.school.auth.dto;

import com.montola.school.auth.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Data
public class UserRegisterRequest {

    @Email
    @NotBlank
    private String email;

    private String phone;

    @NotBlank
    private String password;

    @NotEmpty
    private Set<Role> roles;
}

