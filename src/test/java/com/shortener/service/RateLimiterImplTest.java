package com.shortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimiterImplTest {

    private static final String SHORTENED_URL_CODE = "tEsTacBE";

    private static final String IP_ADDRESS= "127.0.0.1";

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private SetOperations<String, String> setOps;

    @InjectMocks
    RateLimiterImpl rateLimiterImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
    }

    @Test
    void shouldSetTTLWhenCountIsOne() {
        when(redisTemplate.opsForValue().increment(any())).thenReturn(1L);

        rateLimiterImpl.isRateLimitedExceededByCode(SHORTENED_URL_CODE);

        verify(redisTemplate).expire(contains("rate:"), anyLong(), eq(TimeUnit.MINUTES));
    }

    @Test
    void shouldNotRateLimitIfUnderThreshold() {
        when(redisTemplate.opsForValue().increment(any())).thenReturn(50L);

        assertFalse(rateLimiterImpl.isRateLimitedExceededByIp(IP_ADDRESS));
    }

    @Test
    void shouldRateLimitByCodeIfOverThreshold() {
        when(redisTemplate.opsForValue().increment(any())).thenReturn(101L);

        assertTrue(rateLimiterImpl.isRateLimitedExceededByCode(SHORTENED_URL_CODE));
    }

    @Test
    void shouldRateLimitByIpIfOverThreshold() {
        when(redisTemplate.opsForValue().increment(any())).thenReturn(101L);

        assertTrue(rateLimiterImpl.isRateLimitedExceededByIp(IP_ADDRESS));
    }
}