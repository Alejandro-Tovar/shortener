package com.shortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.shortener.entity.ShortenResponse;
import com.shortener.entity.UrlMapping;
import com.shortener.repository.UrlRepository;
import com.shortener.utilities.ShortenerUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

import java.util.List;
import java.util.Optional;

@Service
public class UrlShortenerImpl implements UrlShortener {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerImpl.class);
    private static final long TTL_SECONDS = 86400L;

    private final UrlRepository urlRepository;
    private final RedisCache redisCache;

    @Autowired
    public UrlShortenerImpl(UrlRepository urlRepository,
                            RedisCache redisCache) {
        this.urlRepository = urlRepository;
        this.redisCache = redisCache;
    }

    @Override
    public ShortenResponse shortenUrl(String url)  {
        logger.debug("Shortening URL: {}", url);
        if (!ShortenerUtilities.isValidURL(url)) {
            throw new InvalidUrlException("Invalid Url");
        }
        String shortenedUrl = existingShortenedUrl(url).orElse(null);
        if (shortenedUrl == null) {
            shortenedUrl = saveShortenedUrl(url, ShortenerUtilities.base62generator());
        }
        return new ShortenResponse(shortenedUrl);
    }

    @Override
    public String redirectToUrl(String code) {
        return getFullUrl(code);
    }

    private String getFullUrl(String code) {
        logger.debug("Attempting to retrieve URL: {}", code);
        if (code == null || code.isEmpty()) {
            throw new InvalidUrlException("Invalid Shortened Url");
        }
        String cachedUrl = redisCache.get(code);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        Optional<UrlMapping> urlMappings = urlRepository.findByShortenedUrl(code);
        String queryUrl = urlMappings.orElseThrow(() -> new InvalidUrlException("Invalid Shortened Url")).getUrl();
        redisCache.saveShortenedUrl(code, queryUrl, TTL_SECONDS);
        return queryUrl;
    }

    private String saveShortenedUrl(String url, String shortenedUrl) {
        logger.debug("Attempting to save URL: {}, with code {}", url, shortenedUrl);
        urlRepository.save(new UrlMapping(url, shortenedUrl));
        redisCache.saveShortenedUrl(shortenedUrl, url, TTL_SECONDS);
        return shortenedUrl;
    }

    private Optional<String> existingShortenedUrl(String url) {
        List<UrlMapping> urlMappings = urlRepository.findByUrl(url);
        if (urlMappings == null || urlMappings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(urlMappings.get(0).getShortenedUrl());
    }
}
