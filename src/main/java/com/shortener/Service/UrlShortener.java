package com.shortener.Service;

import com.shortener.Entity.ShortenResponse;

public interface UrlShortener {

    ShortenResponse shortenUrl(String url);

    String redirectToUrl(String code);
}
