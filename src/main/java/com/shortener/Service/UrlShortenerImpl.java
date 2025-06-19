package com.shortener.Service;

import com.shortener.Entity.ShortenRequest;
import com.shortener.Entity.ShortenResponse;
import com.shortener.Entity.UrlMapping;
import com.shortener.Repository.UrlRepository;
import com.shortener.Utilities.ShortenerUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

import java.util.List;
import java.util.Optional;


@Service
public class UrlShortenerImpl implements UrlShortener {

    @Autowired
    UrlRepository urlRepository;

    @Override
    public ShortenResponse shortenUrl(String url)  {
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
        Optional<UrlMapping> urlMappings = urlRepository.findByShortenedUrl(code);
        if (code == null || code.isEmpty() || urlMappings.isEmpty()) {
            throw new InvalidUrlException("Invalid Shortened Url");
        }

        return urlMappings.get().getUrl();
    }

    private String saveShortenedUrl(String url, String shortenedUrl) {
        urlRepository.save(new UrlMapping(url, shortenedUrl));
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
