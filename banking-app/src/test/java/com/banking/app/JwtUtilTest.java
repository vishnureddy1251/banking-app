package com.banking.app;

import com.banking.app.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Nested
    @DisplayName("generateToken and validation")
    class TokenRoundTripTests {

        @Test
        @DisplayName("validateToken returns true for freshly generated token")
        void validToken() {
            String token = jwtUtil.generateToken("alice", "ROLE_USER");

            assertThat(jwtUtil.validateToken(token)).isTrue();
            assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
            assertThat(jwtUtil.extractRole(token)).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("validateToken returns false for malformed token")
        void invalidToken() {
            assertThat(jwtUtil.validateToken("not.a.jwt")).isFalse();
        }

        @Test
        @DisplayName("validateToken returns false for empty string")
        void emptyToken() {
            assertThat(jwtUtil.validateToken("")).isFalse();
        }
    }
}
