package com.shortener.e2etests;

import com.shortener.entity.ShortenRequest;
import com.shortener.entity.ShortenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlShortenerE2ETest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void shortenAndRedirectTest() {
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ShortenRequest request = new ShortenRequest("https://example.com");
        HttpEntity<ShortenRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ShortenResponse> shortenResponse = restTemplate.postForEntity(baseUrl + "/shorten", entity, ShortenResponse.class);

        assertEquals(HttpStatus.OK, shortenResponse.getStatusCode());
        assertNotNull(shortenResponse.getBody());
        String code = shortenResponse.getBody().getShortenedUrl();
        assertFalse(code.isEmpty());

        ResponseEntity<String> redirectResponse = restTemplate.getForEntity(baseUrl + "/s/" + code, String.class);

        assertEquals(HttpStatus.FOUND, redirectResponse.getStatusCode());
    }
}
