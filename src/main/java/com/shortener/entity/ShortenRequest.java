package com.shortener.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ShortenRequest {

    @Getter
    private String url;
}
