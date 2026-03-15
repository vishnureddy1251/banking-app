package com.banking.app.controller;

import com.banking.app.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/redis")
@RequiredArgsConstructor
public class RedisCacheController {

    private final RedisCacheService redisCacheService;

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
}
