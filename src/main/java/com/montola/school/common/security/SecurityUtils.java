package com.montola.school.common.security;

import com.montola.school.auth.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Small helpers for reading the current authentication in places where it may
 * legitimately be absent (public endpoints that optionally carry a token).
 *
 * @author avidewan
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * @return the authenticated user's id, or {@code null} for anonymous requests
     *         (including Spring Security's anonymous authentication).
     */
    public static Long currentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        return null;
    }
}
