package com.shortener.service;

import com.shortener.entity.UrlAnalyticsResponse;
import com.shortener.entity.UrlClick;
import com.shortener.entity.UrlMapping;
import com.shortener.repository.AnalyticsRepository;
import com.shortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnalyticsTrackerImplTest {

    private static final String SHORTENED_URL_CODE = "abc123";

    private static final String IP_ADDRESS = "192.168.1.1";

    private static final int TTL_IN_SECONDS = 86400;

    @Mock
    private RedisCache redisCache;

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private AnalyticsTrackerImpl analyticsTracker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldTrackClicks() {
        analyticsTracker.trackClicks(SHORTENED_URL_CODE, IP_ADDRESS);

        verify(analyticsRepository).save(any(UrlClick.class));
        verify(redisCache).saveUniqueClicks(SHORTENED_URL_CODE, IP_ADDRESS, TTL_IN_SECONDS);
    }

    @Test
    void shouldGetAllClicksToShortenedUrl() {
        when(analyticsRepository.countByShortenedUrl(SHORTENED_URL_CODE)).thenReturn(5L);

        long result = analyticsTracker.getAllClicksToShortenedUrl(SHORTENED_URL_CODE);

        assertEquals(5L, result);
    }

    @Test
    void shouldGetUniqueClicksFromCache() {
        when(redisCache.hasUniqueKey(SHORTENED_URL_CODE)).thenReturn(true);
        when(redisCache.getSetSize(SHORTENED_URL_CODE)).thenReturn(3L);

        long result = analyticsTracker.getUniqueClicks(SHORTENED_URL_CODE);

        assertEquals(3L, result);
        verify(redisCache, times(2)).getSetSize(SHORTENED_URL_CODE); // called twice
    }

    @Test
    void shouldGetUniqueClicksFromDatabaseFallback() {
        when(redisCache.hasUniqueKey(SHORTENED_URL_CODE)).thenReturn(false);
        when(redisCache.getSetSize(SHORTENED_URL_CODE)).thenReturn(0L);
        List<String> ips = List.of("ip1", "ip2");
        when(analyticsRepository.findDistinctIpsByCodeSince(eq(SHORTENED_URL_CODE), any(LocalDateTime.class))).thenReturn(ips);

        long result = analyticsTracker.getUniqueClicks(SHORTENED_URL_CODE);

        assertEquals(2L, result);
        verify(redisCache).saveUniqueClicks(SHORTENED_URL_CODE, "ip1", TTL_IN_SECONDS);
        verify(redisCache).saveUniqueClicks(SHORTENED_URL_CODE, "ip2", TTL_IN_SECONDS);
    }

    @Test
    void shouldGetUrlAnalytics() {
        UrlMapping mapping = new UrlMapping("https://example.com/page", SHORTENED_URL_CODE);
        List<UrlClick> clicks = List.of(new UrlClick(), new UrlClick());
        when(urlRepository.findByShortenedUrl(SHORTENED_URL_CODE)).thenReturn(Optional.of(mapping));
        when(analyticsRepository.findByShortenedUrl(SHORTENED_URL_CODE)).thenReturn(clicks);
        when(redisCache.hasUniqueKey(SHORTENED_URL_CODE)).thenReturn(true);
        when(redisCache.getSetSize(SHORTENED_URL_CODE)).thenReturn(1L);

        UrlAnalyticsResponse response = analyticsTracker.getUrlAnalytics(SHORTENED_URL_CODE);

        assertEquals(SHORTENED_URL_CODE, response.getShortenedUrl());
        assertEquals("https://example.com/page", response.getOriginalUrl());
        assertEquals(2L, response.getTotalClicks());
        assertEquals(1L, response.getUniqueClicks());
    }
}
