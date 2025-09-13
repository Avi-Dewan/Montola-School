package com.montola.school.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.montola.school.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

/**
 * Handles unauthorized access attempts for unauthenticated users.
 * <p>
 * Returns a structured JSON error response with HTTP status 401 (Unauthorized).
 * Logs can be optionally added for monitoring repeated unauthenticated attempts.
 * </p>
 *
 * @author avidewan
 * @date 8/30/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Called when a user attempts to access a secured endpoint without authentication.
     *
     * @param request       the incoming HTTP request
     * @param response      the HTTP response to write to
     * @param authException the exception that triggered this entry point
     * @throws IOException if writing the JSON response fails
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Locale locale = request.getLocale();
        String message = messageSource.getMessage("auth.unauthenticated", null, locale);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                LocalDateTime.now(),
                null
        );
        log.warn("Unauthenticated access attempt to {} from IP {}", request.getRequestURI(), request.getRemoteAddr());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
