package com.shortener.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ShortenRequest {

    @Getter
    private String url;
}
