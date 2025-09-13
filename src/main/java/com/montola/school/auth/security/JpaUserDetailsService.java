package com.montola.school.auth.security;

import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Service for loading {@link UserDetails} from the database.
 * <p>
 * Implements {@link UserDetailsService} to integrate with Spring Security.
 * Fetches user by email and maps roles to {@link SimpleGrantedAuthority}.
 * </p>
 * <p>
 * Must-have for authentication via Spring Security.
 * </p>
 *
 * @author avidewan
 * @date 8/29/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email address.
     *
     * @param email the email of the user
     * @return {@link UserDetails} representing the user for Spring Security
     * @throws UserNotFoundException if no user with the given email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);

                    return new UserNotFoundException();
                });

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
        log.info("Loaded user {} with roles {}", email, authorities);

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}