package com.shortener.service;

import com.shortener.entity.UrlAnalyticsResponse;
import com.shortener.entity.UrlClick;
import com.shortener.entity.UrlMapping;
import com.shortener.exception.NotFoundException;
import com.shortener.repository.AnalyticsRepository;
import com.shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsTrackerImpl implements AnalyticsTracker {

    private static final int TTL_IN_SECONDS = 86400;

    private final RedisCache redisCache;

    private final AnalyticsRepository analyticsRepository;

    private final UrlRepository urlRepository;

    @Autowired
    public AnalyticsTrackerImpl(RedisCache redisCache,
                                AnalyticsRepository analyticsRepository,
                                UrlRepository urlRepository) {
        this.redisCache = redisCache;
        this.analyticsRepository = analyticsRepository;
        this.urlRepository = urlRepository;
    }

    @Override
    public void trackClicks(String code, String ipAddress) {
        analyticsRepository.save(new UrlClick(code, ipAddress, LocalDateTime.now()));
        redisCache.saveUniqueClicks(code, ipAddress, TTL_IN_SECONDS);
    }

    @Override
    public long getAllClicksToShortenedUrl(String code) {
        return analyticsRepository.countByShortenedUrl(code);
    }

    @Override
    public long getUniqueClicks(String code) {
        long size = redisCache.getSetSize(code);
        if (redisCache.hasUniqueKey(code) && size > 0) {
            return redisCache.getSetSize(code);
        }

        LocalDateTime since = LocalDateTime.now().minusDays(1);
        List<String> recentIps = analyticsRepository.findDistinctIpsByCodeSince(code, since);
        for (String ip : recentIps) {
            redisCache.saveUniqueClicks(code, ip, TTL_IN_SECONDS);
        }

        return recentIps.size();
    }

    @Override
    public UrlAnalyticsResponse getUrlAnalytics(String code) {
        UrlMapping urlMapping = urlRepository.findByShortenedUrl(code)
                .orElseThrow(() -> new NotFoundException("Shortened Url Not Found"));
        List<UrlClick> urlClicks = analyticsRepository.findByShortenedUrl(code);
        long uniqueClicks = getUniqueClicks(code);

        return new UrlAnalyticsResponse(code, urlMapping.getUrl(), urlMapping.getCreatedAt(), urlClicks.size(), uniqueClicks);
    }
}
