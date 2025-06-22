package com.shortener.service;

import com.shortener.entity.UrlAnalyticsResponse;

public interface AnalyticsTracker {

    void trackClicks(String code, String ipAddress);

    long getAllClicksToShortenedUrl(String code);

    long getUniqueClicks(String code);

    UrlAnalyticsResponse getUrlAnalytics(String code);
}
