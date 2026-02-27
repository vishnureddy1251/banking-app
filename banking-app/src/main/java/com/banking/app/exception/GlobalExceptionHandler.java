package com.banking.app.exception;

import com.banking.app.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ApiError.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .toList();

        ApiError apiError = ApiError.builder()
                .status(400)
                .error("Validation Failed")
                .message("One or more fields have invalid values")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        log.warn("Validation error on {}: {}", request.getRequestURI(), fieldErrors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleBadRequest(
            RuntimeException ex, HttpServletRequest request) {

        ApiError apiError = buildError(400, "Bad Request", ex.getMessage(), request);
        log.warn("Bad request on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String message = "Required parameter '" + ex.getParameterName() + "' is missing";
        ApiError apiError = buildError(400, "Bad Request", message, request);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = "Parameter '" + ex.getName() + "' should be of type "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        ApiError apiError = buildError(400, "Bad Request", message, request);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ApiError> handleUnauthorized(
            RuntimeException ex, HttpServletRequest request) {

        ApiError apiError = buildError(401, "Unauthorized", ex.getMessage(), request);
        log.warn("Unauthorized access on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        ApiError apiError = buildError(403, "Forbidden",
                "You don't have permission to access this resource", request);
        log.warn("Access denied on {}", request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AccountNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException ex, HttpServletRequest request) {

        ApiError apiError = buildError(404, "Not Found", ex.getMessage(), request);
        log.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(
            NoResourceFoundException ex, HttpServletRequest request) {

        ApiError apiError = buildError(404, "Not Found",
                "Endpoint not found: " + request.getMethod() + " " + request.getRequestURI(), request);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        String message = "Method " + ex.getMethod() + " is not supported for this endpoint. "
                + "Supported methods: " + String.join(", ",
                ex.getSupportedMethods() != null ? ex.getSupportedMethods() : new String[]{});
        ApiError apiError = buildError(405, "Method Not Allowed", message, request);
        return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(
            DuplicateResourceException ex, HttpServletRequest request) {

        ApiError apiError = buildError(409, "Conflict", ex.getMessage(), request);
        log.warn("Duplicate resource on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiError> handleInsufficientBalance(
            InsufficientBalanceException ex, HttpServletRequest request) {

        ApiError apiError = buildError(422, "Unprocessable Entity", ex.getMessage(), request);
        log.warn("Insufficient balance on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(
            ServiceUnavailableException ex, HttpServletRequest request) {

        ApiError apiError = buildError(503, "Service Unavailable", ex.getMessage(), request);
        log.error("Service unavailable on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllOtherExceptions(
            Exception ex, HttpServletRequest request) {

        ApiError apiError = buildError(500, "Internal Server Error",
                "An unexpected error occurred. Please try again later.", request);
        log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiError buildError(int status, String error, String message, HttpServletRequest request) {
        return ApiError.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}