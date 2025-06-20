package com.shortener.Service;

public interface RedisCache {

    void save(String key, String value, long ttlInSeconds);

    String get(String key);
}
