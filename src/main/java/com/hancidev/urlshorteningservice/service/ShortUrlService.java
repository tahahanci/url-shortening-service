package com.hancidev.urlshorteningservice.service;

import com.hancidev.urlshorteningservice.dto.request.CreateShortUrl;
import com.hancidev.urlshorteningservice.dto.response.ShortUrlResponse;

public interface ShortUrlService {

    ShortUrlResponse generateShortURL(CreateShortUrl shortUrl);

    ShortUrlResponse getOriginalUrl(String originalUrl);

    ShortUrlResponse updateShortURL(CreateShortUrl createShortUrl, String shortCode);

    void deleteURL(String shortCode);
}
