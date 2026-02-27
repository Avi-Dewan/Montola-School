package com.montola.school.auth.dto;

import com.montola.school.auth.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

/**
 * @author avidewan
 * @date 2/23/26
 */
@Data
public class AdminRegistrationRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    private String phone;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{5,}$",
            message = "Password must be at least 5 characters long and include at least one letter and one number"
    )
    private String password;

    @NotEmpty
    private Set<Role> roles;
}
