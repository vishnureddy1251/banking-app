package com.banking.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException ex){

        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
    }
}
