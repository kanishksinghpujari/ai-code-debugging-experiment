package dev.experiment.linkforge.api;

import dev.experiment.linkforge.service.AliasAlreadyExistsException;
import dev.experiment.linkforge.service.LinkNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiError> notFound(LinkNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "LINK_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    public ResponseEntity<ApiError> conflict(AliasAlreadyExistsException exception) {
        return response(HttpStatus.CONFLICT, "ALIAS_ALREADY_EXISTS", exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class,
            ConstraintViolationException.class})
    public ResponseEntity<ApiError> badRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(code, message == null ? status.getReasonPhrase() : message, Instant.now()));
    }
}
