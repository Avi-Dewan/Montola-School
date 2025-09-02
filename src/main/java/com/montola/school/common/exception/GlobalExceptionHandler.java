package com.montola.school.common.exception;

import com.montola.school.common.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author avidewan
 * @date 8/29/25
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // Handle @Valid / @Validated validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, Locale locale) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        String message = messageSource.getMessage("validation.failed", null, locale);

        return buildResponse(HttpStatus.BAD_REQUEST, message, fieldErrors);
    }

    // Handle ConstraintViolationException (e.g., path variables, request params)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, Locale locale) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            fieldErrors.put(field, violation.getMessage());
        });

        String message = messageSource.getMessage("validation.constraint", null, locale);

        return buildResponse(HttpStatus.BAD_REQUEST, message, fieldErrors);
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, Locale locale) {

        String message = messageSource.getMessage("error.illegal.argument", null, locale);

        return buildResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    // Handle unactivated users
    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotActivated(UserNotActivatedException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);

        return buildResponse(HttpStatus.FORBIDDEN, message, null);
    }

    // Verification Token Expired
    @ExceptionHandler(RegistrationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationTokenExpired(RegistrationTokenExpiredException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);

        return buildResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    // Authentication & Authorization
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessageKey(), null, locale);

        return buildResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    @ExceptionHandler(AccessDeniedCustomException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedCustomException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);

        return buildResponse(HttpStatus.FORBIDDEN, message, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, Locale locale) {

        String message = messageSource.getMessage("auth.access.denied", null, locale);

        return buildResponse(HttpStatus.FORBIDDEN, message, null);
    }

    // Handle login failures
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, Locale locale) {

        String message = messageSource.getMessage("auth.invalid.credentials", null, locale);

        return buildResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceExists(ResourceAlreadyExistsException ex, Locale locale) {

        String message = messageSource.getMessage(ex.getMessageKey(), null, locale);

        return buildResponse(HttpStatus.CONFLICT, message, null);
    }

    // Handle all other exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, Locale locale) {

        log.info("Fallback Error: {}", ex.getMessage());

        String message = messageSource.getMessage("error.unexpected", null, locale);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                        String message,
                                                        Map<String, String> fieldErrors) {

        ErrorResponse response = new ErrorResponse(status.value(),
                message,
                LocalDateTime.now(),
                fieldErrors);

        return ResponseEntity.status(status).body(response);
    }
}