package com.banking.app.controller;

import com.banking.app.model.User;
import com.banking.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "1. 🔐 Authentication", description = "Register and login to get JWT token. START HERE!")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody User user) {
        try {
            Map<String, String> response = authService.register(user);
            log.info("REST: Registration request for username {}", user.getUsername());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Login and get JWT token")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            Map<String, String> response = authService.login(username, password);
            log.info("REST: Login request for username {}", username);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }
}
