package com.shortener.service;

public interface RateLimiter {

    boolean isRateLimitedExceededByCode(String key);

    boolean isRateLimitedExceededByIp(String key);
}
