package com.montola.school.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author avidewan
 * @date 8/29/25
 */
@Service
public class JwtService {

    private final Key key;
    private final int expirationMinutes;

    public JwtService(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes());
        this.expirationMinutes = props.expirationMinutes();
    }

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

    public String extractSubject(String token) {
        return parse(token).getBody().getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parse(token).getBody());
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    public String[] extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String[].class));
    }


    public boolean isValid(String token, String subject) {
        try {
            final var claims = parse(token).getBody();

            return subject.equals(claims.getSubject()) && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}