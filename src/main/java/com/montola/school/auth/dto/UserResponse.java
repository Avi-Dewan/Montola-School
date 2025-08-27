package com.montola.school.auth.dto;

import com.montola.school.auth.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
/**
 * @author avidewan
 * @date 8/27/25
 */

@Data
public class UserResponse {

    private Long id;

    private String email;

    private Set<Role> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

