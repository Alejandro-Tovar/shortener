package com.shortener.service;

import com.shortener.entity.UrlClick;
import com.shortener.repository.UrlClickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsTrackerImpl implements AnalyticsTracker {

    private static final int TTL_IN_SECONDS = 86400;

    private final RedisCache redisCache;

    private final UrlClickRepository urlClickRepository;

    @Autowired
    public AnalyticsTrackerImpl(RedisCache redisCache,
                                UrlClickRepository urlClickRepository) {
        this.redisCache = redisCache;
        this.urlClickRepository = urlClickRepository;
    }

    @Override
    public void trackClicks(String code, String ipAddress) {
        urlClickRepository.save(new UrlClick(code, ipAddress, LocalDateTime.now()));
        redisCache.saveUniqueClicks(code, ipAddress, TTL_IN_SECONDS);
    }

    @Override
    public long getAllClicksToShortenedUrl(String code) {
        return urlClickRepository.countByShortenedUrl(code);
    }

    @Override
    public long getUniqueClicks(String code) {
        long size = redisCache.getSetSize(code);
        if (redisCache.hasUniqueKey(code) && size > 0) {
            return redisCache.getSetSize(code);
        }

        LocalDateTime since = LocalDateTime.now().minusDays(1);
        List<String> recentIps = urlClickRepository.findDistinctIpsByCodeSince(code, since);
        for (String ip : recentIps) {
            redisCache.saveUniqueClicks(code, ip, TTL_IN_SECONDS);
        }

        return recentIps.size();
    }
}
