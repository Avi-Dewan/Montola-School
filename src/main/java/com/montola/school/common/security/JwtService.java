package com.montola.school.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for generating, parsing, and validating JWT tokens.
 * <p>
 * Supports extracting claims, user ID, roles, and verifying token validity.
 * Logs invalid token parsing attempts for security monitoring.
 * </p>
 *
 * Usage:
 * <pre>
 * {@code
 * String token = jwtService.generateToken(email, Map.of("roles", roles));
 * boolean valid = jwtService.isValid(token, email);
 * String subject = jwtService.extractSubject(token);
 * }
 * </pre>
 *
 * @author avidewan
 * @date 8/29/25
 */
@Service
@Slf4j
public class JwtService {

    private final Key key;
    private final int expirationMinutes;

    public JwtService(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes());
        this.expirationMinutes = props.expirationMinutes();
    }

    /**
     * Generates a signed JWT token with custom claims and expiration.
     *
     * @param subject the token subject, usually the user's email
     * @param claims  additional claims to include in the token
     * @return a signed JWT token string
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationMinutes * 60L)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject (usually email) from a JWT token.
     *
     * @param token JWT token string
     * @return subject contained in the token
     */
    public String extractSubject(String token) {
        return parse(token).getBody().getSubject();
    }

    /**
     * Extracts a specific claim using a resolver function.
     *
     * @param token    JWT token string
     * @param resolver function to extract the claim
     * @param <T>      type of the claim
     * @return value of the claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parse(token).getBody());
    }

    /**
     * Extracts user ID from the token claims.
     *
     * @param token JWT token string
     * @return user ID as Long
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    /**
     * Extracts roles from the token claims.
     *
     * @param token JWT token string
     * @return array of roles
     */
    public String[] extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String[].class));
    }

    /**
     * Validates the token by checking the subject and expiration.
     * Logs parsing failures for monitoring purposes.
     *
     * @param token   JWT token string
     * @param subject expected subject to match
     * @return true if token is valid and not expired
     */
    public boolean isValid(String token, String subject) {
        try {
            final var claims = parse(token).getBody();
            boolean valid = subject.equals(claims.getSubject()) && claims.getExpiration().after(new Date());

            if (!valid) {
                log.warn("Token validation failed for subject {}. Expired or subject mismatch.", subject);
            }

            return valid;

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token for subject {}. Reason: {}", subject, e.getMessage());

            return false;
        }
    }

    /**
     * Parses the JWT token into claims.
     *
     * @param token JWT token string
     * @return parsed Jws<Claims>
     * @throws JwtException if the token is invalid
     */
    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}