package com.durel.bibliotheque.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.durel.bibliotheque.dto.ApiError;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Centralizes exception-to-HTTP-response mapping.
 *
 * Controllers can focus on HTTP endpoints while this class
 * decides how application exceptions are exposed to clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Registration conflict:
     * email or username already exists.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(
            UserAlreadyExistsException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
         * Handles validation errors produced by @Valid.
         *
         * Example:
         * - invalid email
         * - blank username
         * - invalid password
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationException(
                MethodArgumentNotValidException exception,
                HttpServletRequest request) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError ->
                        fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse("Validation failed");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
        }

    /**
     * Login failure.
     *
     * The same generic message is returned whether the email
     * or password is incorrect.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Builds the common API error representation.
     */
    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }

    /**
         * Handles books that do not exist or are not accessible
         * by the authenticated user.
         */
        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ApiError> handleBookNotFound(
                BookNotFoundException exception,
                HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
        }
}
