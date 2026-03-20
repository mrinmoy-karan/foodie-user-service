package com.foodie.userservice.exception;

import com.foodie.userservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle 403 Forbidden (Security Access Denied)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
            "Forbidden",
            "Do not have access to the resource",
            403
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 2. Handle User Not Found (Your custom exception)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "Not Found",
            ex.getMessage(),
            404
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 3. General Handler for "Everything Else"
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage(),
            500
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                "Unauthorized",
                "Invalid email or password", // Or "Invalid or missing security key" to match your test
                401
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "Bad Request");
        body.put("code", 400);

        // Extracting field-specific errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        body.put("errors", fieldErrors); // Nesting the errors makes it cleaner for Frontend

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


}