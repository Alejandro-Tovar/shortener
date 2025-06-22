package com.shortener.repository;


import com.shortener.entity.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<UrlClick, Long> {

    long countByShortenedUrl(String shortenedUrl);

    @Query("SELECT DISTINCT u.ipAddress FROM UrlClick u WHERE u.shortenedUrl = :code AND u.timestamp >= :since")
    List<String> findDistinctIpsByCodeSince(@Param("code") String code, @Param("since") LocalDateTime since);

    List<UrlClick> findByShortenedUrl(String shortenedUrl);
}
