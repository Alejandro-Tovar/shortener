package com.shortener.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCacheImpl implements RedisCache {

    private static final String SHORTENED_URL_TAG = "short:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void save(String key, String value, long ttlInSeconds) {
        redisTemplate.opsForValue().set(SHORTENED_URL_TAG + key, value, Duration.ofSeconds(ttlInSeconds));
    }

    public String get(String key) {
        System.out.println("Retrieving " + SHORTENED_URL_TAG + key);
        return redisTemplate.opsForValue().get(SHORTENED_URL_TAG + key);
    }
}
