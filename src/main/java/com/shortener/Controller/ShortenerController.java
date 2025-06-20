package com.shortener.Controller;

import com.shortener.Service.UrlShortener;
import com.shortener.Entity.ShortenRequest;
import com.shortener.Entity.ShortenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class ShortenerController {

    @Autowired
    private UrlShortener urlShortener;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortener(@RequestBody ShortenRequest url) {
        return new ResponseEntity<>(urlShortener.shortenUrl(url.getUrl()), HttpStatus.OK);
    }

    @GetMapping("/s/{code}")
    public RedirectView redirectToFullUrl(@PathVariable String code) {
        return new RedirectView(urlShortener.redirectToUrl(code));
    }
}
