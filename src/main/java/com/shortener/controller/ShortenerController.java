package com.shortener.controller;

import com.shortener.service.AnalyticsTracker;
import com.shortener.service.UrlShortener;
import com.shortener.entity.ShortenRequest;
import com.shortener.entity.ShortenResponse;
import com.shortener.utilities.ShortenerUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class ShortenerController {

    private final UrlShortener urlShortener;

    private final AnalyticsTracker analyticsTracker;

    @Autowired
    public ShortenerController(UrlShortener urlShortener,
                               AnalyticsTracker analyticsTracker) {
        this.urlShortener = urlShortener;
        this.analyticsTracker = analyticsTracker;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortener(@RequestBody ShortenRequest url) {
        return new ResponseEntity<>(urlShortener.shortenUrl(url.getUrl()), HttpStatus.OK);
    }

    @GetMapping("/s/{code}")
    public RedirectView redirectToFullUrl(@PathVariable String code, HttpServletRequest request) {
        analyticsTracker.trackClicks(code, ShortenerUtilities.getClientIp(request));
        return new RedirectView(urlShortener.redirectToUrl(code));
    }
}
