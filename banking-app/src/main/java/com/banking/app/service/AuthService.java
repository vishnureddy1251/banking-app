package com.banking.app.service;

import com.banking.app.model.User;
import com.banking.app.repository.UserRepository;
import com.banking.app.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);
        log.info("User registered: {} with role {}", user.getUsername(), user.getRole());

        return Map.of("message", "User registered successfully");
    }

    public Map<String, String> login(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            log.info("User logged in: {}", username);

            return Map.of(
                    "token", token,
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "message", "Login successful"
            );

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
