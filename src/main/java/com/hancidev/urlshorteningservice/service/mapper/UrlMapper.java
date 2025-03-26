package com.hancidev.urlshorteningservice.service.mapper;

import com.hancidev.urlshorteningservice.dto.request.CreateShortUrl;
import com.hancidev.urlshorteningservice.dto.response.ShortUrlResponse;
import com.hancidev.urlshorteningservice.entity.ShortUrl;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UrlMapper {

    public ShortUrl shortUrlFromCreateShortUrl(CreateShortUrl from) {
        return ShortUrl.builder()
                .urlID(UUID.randomUUID().toString())
                .originalUrl(from.url())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ShortUrlResponse shortUrlResponseFromShortUrl(ShortUrl from) {
        return ShortUrlResponse.builder()
                .urlID(from.getUrlID())
                .url(from.getOriginalUrl())
                .shortCode(from.getShortCode())
                .createdAt(from.getCreatedAt())
                .updatedAt(from.getUpdatedAt())
                .build();
    }
}
