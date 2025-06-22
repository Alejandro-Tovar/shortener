package com.shortener.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Schema(description = "Analytics response for a shortened URL")
public class UrlAnalyticsResponse {

    @Getter
    private String shortenedUrl;

    @Getter
    private String originalUrl;

    @Getter
    private LocalDateTime createdAt;

    @Getter
    private long totalClicks;

    @Getter
    private long uniqueClicks;
}
