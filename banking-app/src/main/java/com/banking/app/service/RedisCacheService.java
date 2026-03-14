package com.banking.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long ttlMinutes) {
        redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
        log.info("Redis SET: {} (TTL: {}min)", key, ttlMinutes);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        log.info("Redis SET: {} (no TTL)", key);
    }
}
