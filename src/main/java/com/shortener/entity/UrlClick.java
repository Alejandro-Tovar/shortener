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
public class UrlClick {

    public UrlClick(String shortenedUrl,
                    String ipAddress,
                    LocalDateTime timestamp) {
        this.shortenedUrl = shortenedUrl;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String shortenedUrl;

    @Getter
    private String ipAddress;

    @Getter
    private LocalDateTime timestamp;
}