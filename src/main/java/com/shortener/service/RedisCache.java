package com.shortener.service;

public interface RedisCache {

    void saveUniqueClicks(String key, String value, long ttlInSeconds);

    void saveShortenedUrl(String key, String value, long ttlInSeconds);

    String get(String key);

    long getSetSize(String key);

    boolean hasUniqueKey(String key);
}
