package com.shortener.service;

public interface AnalyticsTracker {

    void trackClicks(String code, String ipAddress);

    long getAllClicksToShortenedUrl(String code);

    long getUniqueClicks(String code);
}
