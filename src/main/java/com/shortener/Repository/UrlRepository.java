package com.shortener.Repository;

import com.shortener.Entity.UrlMapping;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    List<UrlMapping> findByUrl(String url);

    Optional<UrlMapping> findByShortenedUrl(String shortenedUrl);
}
