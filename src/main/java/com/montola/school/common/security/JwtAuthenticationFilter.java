package com.montola.school.common.security;

import com.montola.school.common.exception.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Filter that intercepts HTTP requests to validate JWT tokens.
 * <p>
 * This filter checks:
 * <ul>
 *     <li>If the request path is whitelisted (skipped authentication)</li>
 *     <li>If the Authorization header contains a valid Bearer token</li>
 *     <li>If the token is valid and not expired, it sets the {@link SecurityContextHolder} authentication</li>
 * </ul>
 *
 * Throws {@link ResourceNotFoundException} if token is missing,
 * {@link TokenExpiredException} if token is invalid or expired.
 *
 * Logs authentication failures and token validation issues for monitoring.
 * </p>
 *
 * @author avidewan
 * @date 8/29/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        try {
            String path = request.getRequestURI();
            String method = request.getMethod();

            AntPathMatcher pathMatcher = new AntPathMatcher();

            // Skip JWT authentication for whitelisted paths
            boolean isWhitelisted = securityProperties.getWhiteList()
                    .stream()
                    .anyMatch(rule -> {
                        boolean match = pathMatcher.match(rule.pattern(), path);
                        boolean methodMatch = (rule.method() == null || rule.method().equalsIgnoreCase(method));
                        return match && methodMatch;
                    });

            if (isWhitelisted) {
                log.debug("Path {} is whitelisted, skipping JWT check", path);
                chain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or malformed Authorization header for request to {}", path);

                throw new TokenMissingException("auth.token.missing");
            }

            String token = authHeader.substring(7);
            String email;

            try {
                email = jwtService.extractSubject(token);

                if (!jwtService.isValid(token, email)) {
                    log.warn("Expired or invalid token for user {} on path {}", email, path);

                    throw new TokenExpiredException("auth.token.expired");
                }

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userDetailsService.loadUserByUsername(email);

                    var authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("JWT authentication successful for user {} on path {}", email, path);
                }

            } catch (JwtException e) {
                log.error("JWT parsing failed for request to {}. Reason: {}", path, e.getMessage());

                throw new TokenExpiredException("auth.token.expired");

            } catch (UsernameNotFoundException e) {
                log.error("User not found for token on path {}. Reason: {}", path, e.getMessage());

                throw new UserNotFoundException();
            }

            chain.doFilter(request, response);

        } catch (AuthenticationException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}