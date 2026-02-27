package com.montola.school.common.exception;

import com.montola.school.common.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Centralized exception handling for the application.
 * <p>
 * Handles all custom, Spring, and generic exceptions and returns structured
 * JSON responses with appropriate HTTP status codes.
 * Logging is included for monitoring and debugging.
 * </p>
 *
 * @author avidewan
 * @date 8/29/25
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // ----------------- Validation Exceptions -----------------

    /**
     * Handles @Valid / @Validated validation failures in request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, Locale locale) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        String message = messageSource.getMessage("validation.failed", null, locale);
        log.warn("Validation failed: {}", fieldErrors);

        return buildResponse(HttpStatus.BAD_REQUEST, message, fieldErrors);
    }

    /**
     * Handles validation errors from path variables or request parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, Locale locale) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            fieldErrors.put(field, violation.getMessage());
        });

        String message = messageSource.getMessage("validation.constraint", null, locale);
        log.warn("Constraint violation: {}", fieldErrors);

        return buildResponse(HttpStatus.BAD_REQUEST, message, fieldErrors);
    }

    // ----------------- Illegal / Custom Argument Exceptions -----------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, Locale locale) {
        String message = ex.getMessage();

        if (message == null || message.isEmpty()) {
            message = messageSource.getMessage("error.illegal.argument", null, "Invalid argument", locale);
        }

        log.warn("Illegal argument exception: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotActivated(UserNotActivatedException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);
        log.info("Unactivated user attempt: {}", ex.getMessage());

        return buildResponse(HttpStatus.FORBIDDEN, message, null);
    }

    @ExceptionHandler(RegistrationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationTokenExpired(RegistrationTokenExpiredException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);
        log.info("Expired registration token: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    // ----------------- Authentication & Authorization -----------------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessageKey(), null, locale);
        log.warn("Authentication failed: {}", ex.getMessageKey());

        return buildResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    @ExceptionHandler(AccessDeniedCustomException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedCustomException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);
        log.warn("Custom access denied: {}", ex.getMessage());

        return buildResponse(HttpStatus.FORBIDDEN, message, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, Locale locale) {

        String message = messageSource.getMessage("auth.access.denied", null, locale);
        log.warn("Access denied: {}", ex.getMessage());

        return buildResponse(HttpStatus.FORBIDDEN, message, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, Locale locale) {

        String message = messageSource.getMessage("auth.invalid.credentials", null, locale);
        log.info("Bad login attempt");

        return buildResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    // ----------------- Resource Exceptions -----------------

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceExists(ResourceAlreadyExistsException ex, Locale locale) {

        String message = messageSource.getMessage(ex.getMessageKey(), null, locale);
        log.info("Resource already exists: {}", ex.getMessageKey());

        return buildResponse(HttpStatus.CONFLICT, message, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, Locale locale) {

        String message = messageSource.getMessage(ex.getMessageKey(), null, "Resource not found", locale);
        log.info("Resource not found: {}", ex.getMessageKey());

        return buildResponse(HttpStatus.NOT_FOUND, message, null);
    }

    // ----------------- File Upload Exceptions -----------------

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex, Locale locale) {
        String message = messageSource.getMessage("upload.file.too.large", null, "File size exceeds the maximum allowed limit.", locale);
        log.warn("Upload size exceeded: {}", ex.getMessage());

        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, message, null);
    }

    // ----------------- Database Exceptions -----------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, Locale locale) {
        log.error("Database integrity violation: ", ex);
        
        String message = messageSource.getMessage("error.database.integrity", null, "Database error: Constraint violation", locale);
        
        // Optionally, we could parse ex.getMostSpecificCause().getMessage() to give more details,
        // but be careful not to expose sensitive DB schema info.
        
        return buildResponse(HttpStatus.CONFLICT, message, null);
    }

    // ----------------- Fallback Exception -----------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, Locale locale) {

        log.error("Fallback Error: ", ex); // Log full stack trace

        String message = messageSource.getMessage("error.unexpected", null, locale);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    // ----------------- Private Utility -----------------

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
