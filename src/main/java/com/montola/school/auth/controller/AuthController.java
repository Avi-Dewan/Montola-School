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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author avidewan
 * @date 8/30/25
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login and registration")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    private final AuthService authService;

    @Operation(summary = "Login and receive a JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email={}", request.email());
        AuthResponse response = authService.login(request);
        log.info("Login successful for email={}", request.email());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        log.info("Registering new user with email={}", request.getEmail());
        User user = userService.createUser(request);
        log.info("Registering new user with email={}", request.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    @Operation(summary = "Activate a registered user")
    @PostMapping("/activate")
    public ResponseEntity<String> activateUser(@Valid @RequestBody ActivationRequest request) {
        log.info("Activating user email={}", request.getEmail());
        userService.activateUser(request.getEmail(), request.getToken());
        log.info("User email={} activated successfully", request.getEmail());

        return ResponseEntity.ok("Account activated successfully");
    }

    @Operation(summary = "Resend activation token")
    @PostMapping("/resend-activation")
    public ResponseEntity<String> resendActivation(@RequestBody EmailRequest request) {
        log.info("Resend activation requested for email={}", request.getEmail());
        userService.resendActivationToken(request.getEmail());
        log.info("Activation token resent for email={}", request.getEmail());

        return ResponseEntity.ok("A new activation link has been sent!");
    }

    @Operation(summary = "Change password for logged-in user")
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Password change requested");
        userService.changePassword(request);
        log.info("Password changed successfully");

        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Request to reset password")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest request) {
        log.info("Password reset requested for email={}", request.getEmail());
        userService.requestPasswordReset(request.getEmail());
        log.info("Password reset link sent to email={}", request.getEmail());

        return ResponseEntity.ok("Password reset link has been sent!");
    }

    @Operation(summary = "Reset password  with token")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("Password reset attempt for email={}", request.getEmail());
        userService.resetPassword(request);
        log.info("Password reset successful for email={}", request.getEmail());

        return ResponseEntity.ok("Password reset successfully!");
    }
}