package com.shortener.controller;

import com.shortener.entity.UrlAnalyticsResponse;
import com.shortener.service.AnalyticsTracker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    private final AnalyticsTracker analyticsTracker;

    @Autowired
    public AnalyticsController(AnalyticsTracker analyticsTracker) {
        this.analyticsTracker = analyticsTracker;
    }

    @Operation(summary = "Counts how many times the shortened URL has been called")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Value found"),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found")
    })
    @GetMapping("/analytics/{code}/clicks")
    public ResponseEntity<Long> getClickCount(@PathVariable String code){
        return new ResponseEntity<>(analyticsTracker.getAllClicksToShortenedUrl(code), HttpStatus.OK);
    }

    @Operation(summary = "Counts the amount of unique times the shortened URL has been called (call per IP)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Value found"),
    })
    @GetMapping("/analytics/{code}/clicks/unique")
    public ResponseEntity<Long> getUniqueClickCount(@PathVariable String code){
        return new ResponseEntity<>(analyticsTracker.getUniqueClicks(code), HttpStatus.OK);
    }

    @Operation(summary = "Retrieves full analytics for a shortened URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found")
    })
    @GetMapping("/analytics/{code}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalyticsForShortenedUrl(@PathVariable String code) {
        return new ResponseEntity<>(analyticsTracker.getUrlAnalytics(code),  HttpStatus.OK);
    }
}
