package com.montola.school.common.config;

import com.montola.school.common.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central security configuration for Montola School application.
 * <p>
 * Configures:
 * <ul>
 *   <li>JWT authentication filter</li>
 *   <li>CORS policy</li>
 *   <li>Password encoding</li>
 *   <li>Exception handling (authentication/authorization)</li>
 *   <li>Stateless session management</li>
 *   <li>Method-level security</li>
 * </ul>
 * </p>
 *
 * @author avidewan
 * @date 8/27/25
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class})
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final SecurityProperties securityProperties;

    /**
     * Password encoder bean using BCrypt.
     * <p>Must-have for secure password hashing.</p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures global CORS policy for the application.
     * <p>Recommended: define allowed origins and credentials to prevent CORS errors.</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(securityProperties.getCorsAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Configures Spring Security filter chain.
     * <p>
     * Key points:
     * <ul>
     *   <li>CSRF disabled (stateless REST API)</li>
     *   <li>Stateless session management</li>
     *   <li>JWT authentication filter added before UsernamePasswordAuthenticationFilter</li>
     *   <li>Whitelist URLs are publicly accessible</li>
     *   <li>All other requests require authentication</li>
     *   <li>Custom exception handling for authentication and access denied</li>
     * </ul>
     * </p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(
                        Customizer.withDefaults()
                )
                .sessionManagement(sm ->
                                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                securityProperties.getWhiteList().
                                        toArray(String[]::new)
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex-> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes the authentication manager bean.
     * <p>Must-have for authentication in services or custom login flows.</p>
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}