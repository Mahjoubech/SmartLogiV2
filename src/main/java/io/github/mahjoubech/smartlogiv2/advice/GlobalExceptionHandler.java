package io.github.mahjoubech.smartlogiv2.advice;

import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.dto.response.ApiResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.ApiResponseError;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus; // ColisStatus howa l'Enum dyalek
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    // 2. Validation / Business Logic Error (400)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex){
        ApiResponse response = new ApiResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400
    }

    // 3. Conflict State (409) - b7al Email/Phone deja moujoudin
    @ExceptionHandler(ConflictStateException.class)
    public ResponseEntity<ApiResponse> handleConflictStateException(ConflictStateException ex){
        ApiResponse response = new ApiResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409
    }


    // 4. GESTION dyal l'Erreurs l'akhrin (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        // DEBUG: Khass ykoun gha f'l'Environment dyal Development
        ex.printStackTrace();

        ApiResponse response = new ApiResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error. Please try again later."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500
    }
}