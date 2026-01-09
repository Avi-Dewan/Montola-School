package com.montola.school.auth.service;

import com.montola.school.auth.dto.ChangePasswordRequest;
import com.montola.school.auth.dto.ResetPasswordRequest;
import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.model.User;
import com.montola.school.auth.security.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing users, including registration, activation,
 * password changes, resets, and user lookups.
 *
 * This interface defines the core user-related business operations.
 * Implementations should ensure proper validation, security checks,
 * and token handling where applicable.
 *
 * @author avidewan
 * @date 8/27/25
 */
public interface UserService {

    /**
     * Creates a new user or reuses an existing unactivated user.
     *
     * If a user with the given email already exists but is not activated,
     * their record will be updated, and a new activation token will be issued.
     *
     * @param request the registration request containing user details.
     * @return the newly created or updated user entity.
     * @throws com.montola.school.common.exception.ResourceAlreadyExistsException
     *          if the user already exists and is activated.
     */
    User createUser(UserRegisterRequest request);

    /**
     * Activates a user account using an activation token.
     *
     * @param email the user’s email.
     * @param token the activation token.
     * @throws com.montola.school.common.exception.ResourceNotFoundException
     *         if no user or token is found.
     * @throws com.montola.school.common.exception.ResourceAlreadyExistsException
     *         if the user is already activated.
     * @throws com.montola.school.common.exception.RegistrationTokenExpiredException
     *         if the token is expired.
     */
    void activateUser(String email, String token);

    /**
     * Resends a new activation token to the given email.
     *
     * @param email the user’s email.
     * @throws com.montola.school.common.exception.UserNotFoundException
     *         if no user is found with the given email.
     * @throws com.montola.school.common.exception.ResourceAlreadyExistsException
     *         if the user is already activated.
     */
    void resendActivationToken(String email);

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param request contains the old and new passwords.
     * @throws com.montola.school.common.exception.ResourceNotFoundException
     *         if the current user cannot be found.
     * @throws com.montola.school.common.exception.InvalidCredentialsException
     *         if the old password does not match the stored password.
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * Issues a password reset token for the given email.
     *
     * @param email the user’s email requesting a password reset.
     * @throws com.montola.school.common.exception.ResourceNotFoundException
     *         if no user is found with the given email.
     */
    void requestPasswordReset(String email);

    /**
     * Resets the password for a user using a valid reset token.
     *
     * @param request contains the email, token, and new password.
     * @throws com.montola.school.common.exception.ResourceNotFoundException
     *         if no user is found with the given email.
     * @throws com.montola.school.common.exception.TokenExpiredException
     *         if the reset token is expired.
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Finds a user by their email.
     *
     * @param email the email to search for.
     * @return an {@link Optional} containing the user if found, otherwise empty.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their ID.
     *
     * @param id the user’s ID.
     * @return an {@link Optional} containing the user if found, otherwise empty.
     */
    Optional<User> findById(Long id);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email to check.
     * @return {@code true} if the email exists, otherwise {@code false}.
     */
    boolean emailExists(String email);

    /**
     * Finds all users with the given role.
     *
     * @param role the role to search for.
     * @return a list of users with the specified role.
     */
    List<User> findAllByRolesContaining(Role role);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users.
     */
    List<User> findAll();

    /**
     * Retrieves the currently authenticated user's details from the security context.
     *
     * @return the current {@link CustomUserDetails}.
     * @throws IllegalStateException if the user is not authenticated.
     */
    CustomUserDetails getCurrentUserDetails();

    /**
     * Retrieves the currently authenticated {@link User} entity from the database.
     *
     * @return the current {@link User} entity.
     * @throws com.montola.school.common.exception.ResourceNotFoundException if the user cannot be found.
     * @throws IllegalStateException if the user is not authenticated.
     */
    User getCurrentUser();

    void updateProfilePicture(Long userId, MultipartFile file);

    byte[] getProfilePicture(Long userId);
}