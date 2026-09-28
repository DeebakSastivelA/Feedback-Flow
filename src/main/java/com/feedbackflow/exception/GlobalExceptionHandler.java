package com.feedbackflow.exception;

import com.feedbackflow.dto.ApiResponses;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponses.Message> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponses.Message(ex.getMessage()));
    }

    @ExceptionHandler({DuplicateFeedbackException.class, ResourceConflictException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiResponses.Message> conflict(RuntimeException ex) {
        String message = ex instanceof DataIntegrityViolationException
            ? "A record with these unique values already exists or is still referenced."
            : Objects.toString(ex.getMessage(), "A resource conflict occurred.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponses.Message(message));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponses.Message> stateConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponses.Message(ex.getMessage()));
    }

    @ExceptionHandler({FormClosedException.class, InvalidCredentialsException.class})
    public ResponseEntity<ApiResponses.Message> badRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ApiResponses.Message(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponses.Message> invalidInput(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiResponses.Message(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponses.ValidationError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiResponses.ValidationError("Please correct the highlighted fields.", fields));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponses.Message> unreadableRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new ApiResponses.Message("Request body is invalid or contains an incorrectly formatted value."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponses.Message> unexpected(Exception ex) {
        return ResponseEntity.internalServerError().body(new ApiResponses.Message("An unexpected error occurred."));
    }
}