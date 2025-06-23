package com.shortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterImpl implements RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterImpl.class);
    private static final String RATE_LIMIT_TAG = "rate:";
    private static final String IP_TAG = "ip:";
    private static final Long MAX_LIMIT = 100L;
    private static final Long TTL_SHORTENED_URL = 10L;
    private static final Long TTL_SHORTENED_IP = 10L;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RateLimiterImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isRateLimitedExceededByCode(String key) {
        return isRateLimitedExceeded(RATE_LIMIT_TAG + key, TTL_SHORTENED_URL);
    }

    @Override
    public boolean isRateLimitedExceededByIp(String key) {
        return isRateLimitedExceeded(RATE_LIMIT_TAG + IP_TAG + key, TTL_SHORTENED_IP);
    }

    private boolean isRateLimitedExceeded(String fullKey, long ttl) {
        logger.debug("Checking for rate limit on, fullKey: {}, with a ttl: {}", fullKey, ttl);
        Long count = redisTemplate.opsForValue().increment(fullKey);

        if (count == 1L) {
            redisTemplate.expire(fullKey, ttl, TimeUnit.MINUTES);
        }

        return count > MAX_LIMIT;
    }
}
