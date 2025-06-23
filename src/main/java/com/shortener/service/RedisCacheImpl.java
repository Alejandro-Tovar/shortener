package com.shortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCacheImpl implements RedisCache {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheImpl.class);
    private static final String SHORTENED_URL_TAG = "short:";
    private static final String UNIQUE_CLICKS_TAG = "clicks:unique:";

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisCacheImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveUniqueClicks(String key, String value, long ttlInSeconds) {
        saveSet(UNIQUE_CLICKS_TAG + key, value, ttlInSeconds);
    }

    @Override
    public void saveShortenedUrl(String key, String value, long ttlInSeconds) {
        save(SHORTENED_URL_TAG + key, value, ttlInSeconds);
    }

    @Override
    public String get(String key) {
        log.debug("Attempting to get key {}", key);
        return redisTemplate.opsForValue().get(SHORTENED_URL_TAG + key);
    }

    @Override
    public long getSetSize(String key) {
        return redisTemplate.opsForSet().size(UNIQUE_CLICKS_TAG + key);
    }

    @Override
    public boolean hasUniqueKey(String key) {
        return hasKey(UNIQUE_CLICKS_TAG + key);
    }

    private boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    private void save(String key, String value, long ttlInSeconds) {
        log.debug("Attempting to save key {} with value {} and TTL {}", key, value,  ttlInSeconds);
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlInSeconds));
    }

    private void saveSet(String key, String value, long ttlInSeconds) {
        log.debug("Attempting to save key set {} with value {} and TTL {}", key, value,  ttlInSeconds);
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, Duration.ofSeconds(ttlInSeconds));
    }
}
