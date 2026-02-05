package com.ltuc.docconnect.exception;

import com.ltuc.docconnect.dto.response.ApiResponse;
import com.ltuc.docconnect.util.Messages;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ApiResponse<T>> createErrorResponse(HttpStatus status, String message, String errorCode) {
        ApiResponse<T> response = ApiResponse.failure(message, errorCode);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "E404_NOT_FOUND");
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessLogicException(BusinessLogicException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "E400_BUSINESS");
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflictException(ConflictException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "E409_BUSINESS_CONFLICT");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "E403_FORBIDDEN");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "E400_BAD_REQUEST");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .data(errors)
                .message(Messages.INPUT_VALIDATION_FAILED)
                .errorCode("E400_VALIDATION")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
        String errorMessage = String.format(
                Messages.TYPE_MISMATCH_TEMPLATE,
                ex.getValue(),
                ex.getName(),
                requiredType
        );
        return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "E400_TYPE_MISMATCH");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, Messages.DATA_INTEGRITY_VIOLATION, "E409_CONFLICT");
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request or empty body", "E400_MALFORMED_JSON");
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), "E405_METHOD_NOT_ALLOWED");
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaTypeNotSupported(org.springframework.web.HttpMediaTypeNotSupportedException ex) {
        return createErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), "E415_UNSUPPORTED_MEDIA_TYPE");
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", "E401_BAD_CREDENTIALS");
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage(), "E401_AUTH_FAILED");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        String message = String.format(Messages.UNEXPECTED_SERVER_ERROR, ex.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, "E500_GENERIC");
    }


}