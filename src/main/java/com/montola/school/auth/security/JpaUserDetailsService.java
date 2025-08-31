package com.montola.school.auth.security;

import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author avidewan
 * @date 8/29/25
 */
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}