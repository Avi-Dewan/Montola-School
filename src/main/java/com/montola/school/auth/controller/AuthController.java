package com.montola.school.auth.controller;

import com.montola.school.auth.dto.AuthResponse;
import com.montola.school.auth.dto.LoginRequest;
import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.dto.UserResponse;
import com.montola.school.auth.mapper.UserMapper;
import com.montola.school.auth.model.User;
import com.montola.school.auth.service.AuthService;
import com.montola.school.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and info")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    private final AuthService authService;

    @Operation(summary = "Login and receive a JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {

        User user = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    @Operation(summary = "Get user info by email")
    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUser(@RequestParam String email) {
        Optional<User> userOpt = userService.findByEmail(email);

        return userOpt
                .map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Check if email exists")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }
}
