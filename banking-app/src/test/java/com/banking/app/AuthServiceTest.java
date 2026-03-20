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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        @Test
        @DisplayName("Should throw exception when username already taken")
        void shouldThrowExceptionForDuplicateUsername() {

            when(userRepository.existsByUsername("arjun")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(testUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already taken");
        }

        @Test
        @DisplayName("Should NOT save user when username is duplicate")
        void shouldNotSaveUserWhenDuplicate() {

            when(userRepository.existsByUsername("arjun")).thenReturn(true);

            try {
                authService.register(testUser);
            } catch (IllegalArgumentException ignored) {
            }

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should assign default role ROLE_USER when role is null")
        void shouldAssignDefaultRoleWhenNull() {

            testUser.setRole(null);
            when(userRepository.existsByUsername("arjun")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            authService.register(testUser);

            assertThat(testUser.getRole()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("Should assign default role ROLE_USER when role is empty")
        void shouldAssignDefaultRoleWhenEmpty() {

            testUser.setRole("");
            when(userRepository.existsByUsername("arjun")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            authService.register(testUser);

            assertThat(testUser.getRole()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("Should keep ROLE_ADMIN when explicitly set")
        void shouldKeepAdminRoleWhenSet() {
            
            testUser.setRole("ROLE_ADMIN");
            when(userRepository.existsByUsername("arjun")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            authService.register(testUser);

            assertThat(testUser.getRole()).isEqualTo("ROLE_ADMIN");
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

        @Test
        @DisplayName("Should throw BadCredentialsException for wrong password")
        void shouldThrowExceptionForWrongPassword() {

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login("arjun", "wrongpassword"))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid");
        }

        @Test
        @DisplayName("Should throw BadCredentialsException for non-existent user")
        void shouldThrowExceptionForNonExistentUser() {

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login("nonexistent", "pass123"))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Should call authenticationManager.authenticate during login")
        void shouldCallAuthenticationManager() {

            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken("arjun", "pass123"));
            when(userRepository.findByUsername("arjun")).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("token");

            authService.login("arjun", "pass123");

            verify(authenticationManager, times(1)).authenticate(
                    any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Should generate JWT token with correct username and role")
        void shouldGenerateTokenWithCorrectParams() {

            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken("arjun", "pass123"));
            when(userRepository.findByUsername("arjun")).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken("arjun", "ROLE_USER")).thenReturn("token");

            authService.login("arjun", "pass123");

            verify(jwtUtil, times(1)).generateToken("arjun", "ROLE_USER");
        }
        
        @Test
        @DisplayName("Should NOT generate token when authentication fails")
        void shouldNotGenerateTokenOnFailure() {
        
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            try {
                authService.login("arjun", "wrongpassword");
            } catch (BadCredentialsException ignored) {
            }

            verify(jwtUtil, never()).generateToken(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw generic bad credentials when authenticated user is missing in repository")
        void shouldThrowBadCredentialsWhenUserMissingAfterAuthentication() {

            Authentication auth = new UsernamePasswordAuthenticationToken("ghost", "pass123");
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login("ghost", "pass123"))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid username or password");

            verify(jwtUtil, never()).generateToken(anyString(), anyString());
        }

    }
}
