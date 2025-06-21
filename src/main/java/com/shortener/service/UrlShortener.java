package com.shortener.service;

import com.shortener.entity.ShortenResponse;

public interface UrlShortener {

    ShortenResponse shortenUrl(String url);

    String redirectToUrl(String code);
}
