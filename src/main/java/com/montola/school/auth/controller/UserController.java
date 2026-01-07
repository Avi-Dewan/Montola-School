package com.montola.school.auth.controller;

import com.montola.school.auth.dto.UserResponse;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.mapper.UserMapper;
import com.montola.school.auth.model.User;
import com.montola.school.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
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
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponse> users = userService.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        log.debug("Total users found: {}", users.size());

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get all users with a specific role")
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        log.info("Fetching users with role: {}", role);
        List<UserResponse> users = userService.findAllByRolesContaining(role)
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        log.debug("Users found with role {}: {}", role, users.size());

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user info by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == principal.id")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.info("Fetching user by id: {}", id);
        Optional<User> userOpt = userService.findById(id);

        if (userOpt.isPresent()) {
            log.debug("User found with id {}", id);

            return ResponseEntity.ok(userMapper.toResponse(userOpt.get()));

        } else {
            log.warn("User not found with id {}", id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Get user info by email")
    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #email == principal.username")
    public ResponseEntity<UserResponse> getUser(@RequestParam String email) {
        log.info("Fetching user by email: {}", email);
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isPresent()) {
            log.debug("User found with email {}", email);

            return ResponseEntity.ok(userMapper.toResponse(userOpt.get()));

        } else {
            log.warn("User not found with email {}", email);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Check if email exists")
    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.info("Checking if email exists: {}", email);
        boolean exists = userService.emailExists(email);
        log.debug("Email {} exists? {}", email, exists);

        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Upload profile picture")
    @PostMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == principal.id")
    public ResponseEntity<Void> uploadProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType == null || !isValidImageContentType(contentType)) {
            log.warn("Invalid profile picture content type: {}", contentType);
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, and WEBP are allowed.");
        }

        if (file.getSize() > 500 * 1024) {
            log.warn("Profile picture size exceeded 500KB: {}", file.getSize());
            throw new IllegalArgumentException("File size exceeds the 500KB limit for profile pictures.");
        }

        log.info("Uploading profile picture for user id: {}", id);
        userService.updateProfilePicture(id, file);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get profile picture")
    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) {
        log.info("Fetching profile picture for user id: {}", id);
        byte[] photo = userService.getProfilePicture(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setCacheControl("max-age=31536000");

        return new ResponseEntity<>(photo, headers, HttpStatus.OK);
    }

    private boolean isValidImageContentType(String contentType) {
        return Arrays.asList("image/jpeg", "image/png", "image/webp").contains(contentType);
    }
}