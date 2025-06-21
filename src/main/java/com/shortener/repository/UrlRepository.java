package com.shortener.repository;

import com.shortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    List<UrlMapping> findByUrl(String url);

    Optional<UrlMapping> findByShortenedUrl(String shortenedUrl);
}
