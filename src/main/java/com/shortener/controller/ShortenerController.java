package com.shortener.controller;

import com.shortener.exception.RateLimitExceededException;
import com.shortener.service.AnalyticsTracker;
import com.shortener.service.RateLimiter;
import com.shortener.service.UrlShortener;
import com.shortener.entity.ShortenRequest;
import com.shortener.entity.ShortenResponse;
import com.shortener.utilities.ShortenerUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final RateLimiter rateLimiter;

    @Autowired
    public ShortenerController(UrlShortener urlShortener,
                               AnalyticsTracker analyticsTracker,
                               RateLimiter rateLimiter) {
        this.urlShortener = urlShortener;
        this.analyticsTracker = analyticsTracker;
        this.rateLimiter = rateLimiter;
    }

    @Operation(summary = "Generates a shortened code for a valid URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valid request, shortened code returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortener(@RequestBody ShortenRequest url, HttpServletRequest request) {
        if (rateLimiter.isRateLimitedExceededByIp(ShortenerUtilities.getClientIp(request))) {
            throw new RateLimitExceededException("Too many requests from this IP");
        }
        return new ResponseEntity<>(urlShortener.shortenUrl(url.getUrl()), HttpStatus.OK);
    }

    @Operation(summary = "Redirects to original URL")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Found - Redirect to original URL", content = @Content),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found", content = @Content)
    })
    @GetMapping("/s/{code}")
    public RedirectView redirectToFullUrl(@PathVariable String code, HttpServletRequest request) {
        if (rateLimiter.isRateLimitedExceededByCode(code)) {
            throw new RateLimitExceededException("Too many requests for this shortened URL");
        }
        analyticsTracker.trackClicks(code, ShortenerUtilities.getClientIp(request));
        return new RedirectView(urlShortener.redirectToUrl(code));
    }
}
