package com.montola.school.auth.controller;

import com.montola.school.auth.dto.*;
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

/**
 * @author avidewan
 * @date 8/30/25
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login and registration")
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

    @Operation(summary = "Activate a registered user")
    @PostMapping("/activate")
    public ResponseEntity<String> activateUser(@Valid @RequestBody ActivationRequest request) {
        userService.activateUser(request.getEmail(), request.getToken());

        return ResponseEntity.ok("Account activated successfully");
    }

    @Operation(summary = "Resend activation token")
    @PostMapping("/resend-activation")
    public ResponseEntity<String> resendActivation(@RequestBody ResendActivationRequest request) {
        userService.resendActivationToken(request.getEmail());

        return ResponseEntity.ok("A new activation link has been sent!");
    }
}