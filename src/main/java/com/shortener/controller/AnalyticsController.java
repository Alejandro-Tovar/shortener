package com.shortener.controller;

import com.shortener.service.AnalyticsTracker;
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

    @GetMapping("/analytics/{code}/clicks")
    public ResponseEntity<Long> getClickCount(@PathVariable String code){
        return new ResponseEntity<>(analyticsTracker.getAllClicksToShortenedUrl(code), HttpStatus.OK);
    }

    @GetMapping("/analytics/{code}/clicks/unique")
    public ResponseEntity<Long> getUniqueClickCount(@PathVariable String code){
        return new ResponseEntity<>(analyticsTracker.getUniqueClicks(code), HttpStatus.OK);
    }
}
