package com.shortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisCacheImplTest {
    
    private static final String EXAMPLE_URL = "https://example.com/page";
    
    private static final String SHORTENED_URL_CODE = "code123";
    
    private static final String REDIS_KEY_SHORTENED_URL = "short:" + SHORTENED_URL_CODE;

    private static final String IP_ADDRESS = "127.0.0.1";

    private static final String REDIS_UNIQUE_KEY_SHORTENED_URL = "clicks:unique:" + SHORTENED_URL_CODE;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private SetOperations<String, String> setOps;

    @InjectMocks
    private RedisCacheImpl redisCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
    }

    @Test
    void shouldSaveShortenedUrl() {
        redisCache.saveShortenedUrl(SHORTENED_URL_CODE, EXAMPLE_URL, 3600L);
        verify(valueOps).set(REDIS_KEY_SHORTENED_URL, EXAMPLE_URL, Duration.ofSeconds(3600L));
    }

    @Test
    void shouldGetShortenedUrl() {
        when(valueOps.get(REDIS_KEY_SHORTENED_URL)).thenReturn(EXAMPLE_URL);
        String result = redisCache.get(SHORTENED_URL_CODE);

        assert result.equals(EXAMPLE_URL);
    }

    @Test
    void shouldSaveUniqueClicks() {
        redisCache.saveUniqueClicks(SHORTENED_URL_CODE, IP_ADDRESS, 86400L);

        verify(setOps).add(REDIS_UNIQUE_KEY_SHORTENED_URL, IP_ADDRESS);
        verify(redisTemplate).expire(REDIS_UNIQUE_KEY_SHORTENED_URL, Duration.ofSeconds(86400L));
    }

    @Test
    void shouldGetSetSize() {
        when(setOps.size(REDIS_UNIQUE_KEY_SHORTENED_URL)).thenReturn(5L);
        long size = redisCache.getSetSize(SHORTENED_URL_CODE);

        assert size == 5L;
    }

    @Test
    void shouldHaveUniqueKey() {
        when(redisTemplate.hasKey(REDIS_UNIQUE_KEY_SHORTENED_URL)).thenReturn(true);
        boolean exists = redisCache.hasUniqueKey(SHORTENED_URL_CODE);

        assert exists;
    }
}