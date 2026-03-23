package com.banking.app.controller;

import com.banking.app.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "12. 🗄️ Cache", description = "View cache stats and clear caches")
@RequestMapping("/api/v1/redis")
public class RedisCacheController {

    private final RedisCacheService redisCacheService;

    @Operation(summary = "Store a value in Redis")
    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> setValue(@RequestBody Map<String, Object> request) {
        String key = request.get("key").toString();
        Object value = request.get("value");
        long ttl = Long.parseLong(request.getOrDefault("ttlMinutes", "5").toString());

        redisCacheService.set(key, value, ttl);

        return ResponseEntity.ok(Map.of(
                "message", "Stored in Redis",
                "key", key,
                "ttlMinutes", String.valueOf(ttl)
        ));
    }

    @Operation(summary = "Get a value from Redis")
    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, Object>> getValue(@PathVariable String key) {
        Object value = redisCacheService.get(key);
        long ttl = redisCacheService.getTtl(key);

        if (value != null) {
            return ResponseEntity.ok(Map.of(
                    "key", key,
                    "value", value,
                    "ttlSeconds", ttl,
                    "status", "HIT"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "key", key,
                    "status", "MISS",
                    "message", "Key not found in Redis"
            ));
        }
    }

    @Operation(summary = "Delete a key from Redis")
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, Object>> deleteKey(@PathVariable String key) {
        boolean deleted = redisCacheService.delete(key);
        return ResponseEntity.ok(Map.of(
                "key", key,
                "deleted", deleted
        ));
    }

    @Operation(summary = "List keys by pattern")
    @GetMapping("/keys/{pattern}")
    public ResponseEntity<Map<String, Object>> getKeys(@PathVariable String pattern) {
        Set<String> keys = redisCacheService.getKeys(pattern + "*");
        return ResponseEntity.ok(Map.of(
                "pattern", pattern + "*",
                "count", keys.size(),
                "keys", keys
        ));
    }

    @Operation(summary = "Delete keys by pattern")
    @DeleteMapping("/flush/{pattern}")
    public ResponseEntity<Map<String, Object>> flushByPattern(@PathVariable String pattern) {
        long deleted = redisCacheService.deleteByPattern(pattern + "*");
        return ResponseEntity.ok(Map.of(
                "pattern", pattern + "*",
                "deletedCount", deleted
        ));
    }

    @Operation(summary = "View Redis info and stats")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(redisCacheService.getRedisInfo());
    }

    @Operation(summary = "Generate OTP for a user")
    @PostMapping("/otp/generate")
    public ResponseEntity<Map<String, String>> generateOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit OTP

        redisCacheService.storeOtp(username, otp);

        return ResponseEntity.ok(Map.of(
                "username", username,
                "otp", otp,
                "message", "OTP stored in Redis. Expires in 5 minutes.",
                "note", "In production, this would be sent via SMS/email, not returned in response"
        ));
    }

    @Operation(summary = "Verify OTP")
    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otp = request.get("otp");

        boolean valid = redisCacheService.verifyOtp(username, otp);

        return ResponseEntity.ok(Map.of(
                "username", username,
                "valid", valid,
                "message", valid ? "OTP verified successfully" : "Invalid or expired OTP"
        ));
    }

    @Operation(summary = "Simulate failed login attempt")
    @PostMapping("/login-attempt/fail")
    public ResponseEntity<Map<String, Object>> simulateFailedLogin(
            @RequestBody Map<String, String> request) {
        String username = request.get("username");
        int attempts = redisCacheService.incrementFailedAttempts(username);
        boolean locked = redisCacheService.isAccountLocked(username);

        return ResponseEntity.ok(Map.of(
                "username", username,
                "failedAttempts", attempts,
                "locked", locked,
                "message", locked ?
                        "Account locked after 5 failed attempts. Try again in 15 minutes." :
                        "Failed attempt " + attempts + " of 5"
        ));
    }

    @Operation(summary = "Reset failed login attempts")
    @PostMapping("/login-attempt/reset")
    public ResponseEntity<Map<String, String>> resetFailedAttempts(
            @RequestBody Map<String, String> request) {
        String username = request.get("username");
        redisCacheService.resetFailedAttempts(username);
        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Failed login attempts reset"
        ));
    }
}
