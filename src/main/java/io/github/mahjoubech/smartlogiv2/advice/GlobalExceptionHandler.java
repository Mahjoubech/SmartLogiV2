package io.github.mahjoubech.smartlogiv2.advice;

import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import io.github.mahjoubech.smartlogiv2.dto.response.ApiResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.ApiResponseError;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus; // ColisStatus howa l'Enum dyalek
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // === GESTION dyal l'Validation dyal @Valid (400) ===
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseError> handleValidationErrors(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList()); // Collect l'List

        ApiResponseError response = new ApiResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed, please check your input fields.",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // === GESTION dyal Type Mismatch (400) - UUID w Enum ===
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        int status = HttpStatus.BAD_REQUEST.value(); // 400

        if (ex.getRequiredType() != null && ex.getRequiredType().equals(String.class)) {

        }

        if(ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            // Gestion dyal Enum b7al ColisStatus
            String allowedValues = ColisStatus.getAllowedValues();
            ApiResponse response = new ApiResponse(
                    status,
                    "Invalid status format. Allowed values: " + allowedValues + ". Input: " + invalidValue
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ApiResponse response = new ApiResponse(
                status,
                "Invalid parameter format for: " + ex.getName() + ". Value: " + invalidValue
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // === GESTION dyal CUSTOM EXCEPTIONS dyal SDMS ===

    // 1. Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex){
        ApiResponse response = new ApiResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ApiResponse response = new ApiResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden: You don't have permission to access this resource"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    // 2. Validation / Business Logic Error (400)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex){
        ApiResponse response = new ApiResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400
    }

    @ExceptionHandler(ConflictStateException.class)
    public ResponseEntity<ApiResponse> handleConflictStateException(ConflictStateException ex){
        ApiResponse response = new ApiResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409
    }


    @ExceptionHandler(RuntimeException.class )
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiResponse response = new ApiResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}