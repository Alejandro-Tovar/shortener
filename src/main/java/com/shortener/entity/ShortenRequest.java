package com.shortener.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ShortenRequest {

    @Getter
    private String url;
}
