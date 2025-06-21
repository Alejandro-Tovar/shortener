package com.shortener.service;

import com.shortener.entity.UrlMapping;
import com.shortener.repository.UrlRepository;
import com.shortener.entity.ShortenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.util.InvalidUrlException;

class UrlShortenerImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisCacheImpl redisCache;

    @InjectMocks
    private UrlShortenerImpl urlShortener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnShortUrlWhenValidUrl() {
        String inputUrl = "https://example.com/page";
        when(urlRepository.findByUrl(inputUrl)).thenReturn(Collections.emptyList());
        when(urlRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenResponse response = urlShortener.shortenUrl(inputUrl);

        assertNotNull(response);
        assertTrue(response.getShortenedUrl().length() >= 8);
    }

    @Test
    public void shouldReturnExistingShortUrlWhenUrlAlreadyExists() {
        String inputUrl = "https://example.com/page";
        String existingCode = "abc123";
        UrlMapping mapping = new UrlMapping(inputUrl, existingCode);

        when(urlRepository.findByUrl(inputUrl)).thenReturn(List.of(mapping));

        ShortenResponse response = urlShortener.shortenUrl(inputUrl);

        assertEquals(existingCode, response.getShortenedUrl());
        verify(urlRepository, never()).save(any());
    }

    @Test
    public void shortenUrl_shouldThrowException_whenUrlIsInvalid() {
        String badUrl = "not-a-url";
        assertThrows(InvalidUrlException.class, () -> urlShortener.shortenUrl(badUrl));
    }

    @Test
    public void shortenUrl_shouldThrowException_whenUrlIsNull() {
        assertThrows(InvalidUrlException.class, () -> urlShortener.shortenUrl(null));
    }
}
