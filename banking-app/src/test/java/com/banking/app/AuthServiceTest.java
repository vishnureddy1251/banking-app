package com.banking.app;

import com.banking.app.model.User;
import com.banking.app.repository.UserRepository;
import com.banking.app.security.JwtUtil;
import com.banking.app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("arjun");
        testUser.setPassword("pass123");
        testUser.setRole("ROLE_USER");
    }

    @Nested
    @DisplayName("Register")
    class RegisterTests {

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {

            when(userRepository.existsByUsername("arjun")).thenReturn(false);
            when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            Map<String, String> result = authService.register(testUser);

            assertThat(result).containsKey("message");
            assertThat(result.get("message")).isEqualTo("User registered successfully");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should hash password before saving")
        void shouldHashPasswordBeforeSaving() {

            when(userRepository.existsByUsername("arjun")).thenReturn(false);
            when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            authService.register(testUser);

            verify(passwordEncoder, times(1)).encode("pass123");

            assertThat(testUser.getPassword()).isEqualTo("$2a$10$hashedPassword");
        }
    }

    @Nested
    @DisplayName("Login")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully and return token")
        void shouldLoginSuccessfully() {

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken("arjun", "pass123"));
            when(userRepository.findByUsername("arjun")).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken("arjun", "ROLE_USER")).thenReturn("mock-jwt-token-123");

            Map<String, String> result = authService.login("arjun", "pass123");

            assertThat(result).containsKey("token");
            assertThat(result.get("token")).isEqualTo("mock-jwt-token-123");
            assertThat(result.get("username")).isEqualTo("arjun");
            assertThat(result.get("role")).isEqualTo("ROLE_USER");
            assertThat(result.get("message")).isEqualTo("Login successful");
        }

        @Test
        @DisplayName("Should return all required fields in login response")
        void shouldReturnAllFieldsInLoginResponse() {

            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken("arjun", "pass123"));
            when(userRepository.findByUsername("arjun")).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("token123");

            Map<String, String> result = authService.login("arjun", "pass123");

            assertThat(result).containsKeys("token", "username", "role", "message");
            assertThat(result).hasSize(4);
        }

    }
}
