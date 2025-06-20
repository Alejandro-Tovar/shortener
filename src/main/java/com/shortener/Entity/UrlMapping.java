package com.shortener.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class UrlMapping {

    public UrlMapping(String url, String shortenedUrl) {
        this.url = url;
        this.shortenedUrl = shortenedUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    int id;
    @Getter
    String url;
    @Getter
    String shortenedUrl;
}
