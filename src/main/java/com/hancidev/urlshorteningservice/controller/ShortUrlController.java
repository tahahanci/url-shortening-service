package com.hancidev.urlshorteningservice.controller;

import com.hancidev.urlshorteningservice.dto.request.CreateShortUrl;
import com.hancidev.urlshorteningservice.dto.response.ShortUrlResponse;
import com.hancidev.urlshorteningservice.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortUrlResponse> generateShortURL(@RequestBody CreateShortUrl url) {
        return ResponseEntity.ok(shortUrlService.generateShortURL(url));
    }

    @GetMapping("/shorten/{originalUrl}")
    public ResponseEntity<ShortUrlResponse> getOriginalUrl(@PathVariable String originalUrl) {
        return ResponseEntity.ok(shortUrlService.getOriginalUrl(originalUrl));
    }

    @PutMapping("/shorten/{shortCode}")
    public ResponseEntity<ShortUrlResponse> updateOriginalURL(@PathVariable String shortCode, @RequestBody CreateShortUrl url) {
        return ResponseEntity.ok(shortUrlService.updateShortURL(url, shortCode));
    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<Void> deleteURL(@PathVariable String shortCode) {
        shortUrlService.deleteURL(shortCode);
        return ResponseEntity.noContent().build();
    }
}
