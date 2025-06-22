package com.shortener.handler;

import com.shortener.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.InvalidUrlException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, String>> handleInvalidUrl(InvalidUrlException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("Invalid Url", ex.getMessage());
        return ResponseEntity.badRequest().body(errorBody);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundUrl(NotFoundException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("Url Not Found", ex.getMessage());
        return ResponseEntity.badRequest().body(errorBody);
    }
}
