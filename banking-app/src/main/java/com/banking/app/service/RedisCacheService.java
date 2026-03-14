package com.banking.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
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

    public Object get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            log.info("Redis HIT: {}", key);
        } else {
            log.info("Redis MISS: {}", key);
        }
        return value;
    }

    public boolean delete(String key) {
        Boolean deleted = redisTemplate.delete(key);
        log.info("Redis DELETE: {} (result: {})", key, deleted);
        return Boolean.TRUE.equals(deleted);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean setExpiry(String key, long ttlMinutes) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, Duration.ofMinutes(ttlMinutes)));
    }

    public long getTtl(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -1;
    }

    public long deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            Long count = redisTemplate.delete(keys);
            log.info("Redis DELETE PATTERN: {} → {} keys deleted", pattern, count);
            return count != null ? count : 0;
        }
        return 0;
    }

    public Set<String> getKeys(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? keys : Set.of();
    }
}
