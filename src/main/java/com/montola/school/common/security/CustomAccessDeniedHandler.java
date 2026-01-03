package com.montola.school.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.montola.school.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Handles forbidden access attempts for authenticated users lacking required roles/permissions.
 * <p>
 * Returns a structured JSON error response with HTTP status 403 (Forbidden).
 * Optional logging can help monitor repeated unauthorized access attempts.
 * </p>
 *
 * @author avidewan
 * @date 8/30/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    /**
     * Called when an authenticated user tries to access a resource they are not authorized for.
     *
     * @param request                 the incoming HTTP request
     * @param response                the HTTP response to write to
     * @param accessDeniedException   the exception that triggered this handler
     * @throws IOException if writing the JSON response fails
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        Locale locale = request.getLocale();
        String message = messageSource.getMessage("auth.access.denied", null, locale);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                message,
                LocalDateTime.now(),
                null
        );

        log.warn("Access denied to {} from user {} (IP {})",
                request.getRequestURI(),
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "unknown",
                request.getRemoteAddr()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
