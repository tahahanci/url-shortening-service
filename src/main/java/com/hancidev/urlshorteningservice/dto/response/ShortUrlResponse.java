package com.hancidev.urlshorteningservice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ShortUrlResponse(String urlID, String url, String shortCode,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
}
