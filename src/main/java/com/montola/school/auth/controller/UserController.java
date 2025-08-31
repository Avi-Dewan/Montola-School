package com.montola.school.auth.controller;

import com.montola.school.auth.dto.UserResponse;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.mapper.UserMapper;
import com.montola.school.auth.model.User;
import com.montola.school.auth.service.UserService;
import com.montola.school.auth.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @date 8/30/25
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints to manage users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get all users with a specific role")
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> users = userService.findAllByRolesContaining(role)
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user info by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == principal.id")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);

        return userOpt
                .map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Get user info by email")
    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #email == principal.username")
    public ResponseEntity<UserResponse> getUser(@RequestParam String email) {
        Optional<User> userOpt = userService.findByEmail(email);

        return userOpt
                .map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Check if email exists")
    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }
}
