package com.shortener.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class UrlMapping {

    public UrlMapping(String url,
                      String shortenedUrl) {
        this.url = url;
        this.shortenedUrl = shortenedUrl;
        this.createdAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    int id;

    @Getter
    String url;

    @Getter
    String shortenedUrl;

    @Getter
    LocalDateTime createdAt;
}
