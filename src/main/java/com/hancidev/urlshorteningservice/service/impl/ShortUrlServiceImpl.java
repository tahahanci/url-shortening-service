package com.hancidev.urlshorteningservice.service.impl;

import com.hancidev.urlshorteningservice.dto.request.CreateShortUrl;
import com.hancidev.urlshorteningservice.dto.response.ShortUrlResponse;
import com.hancidev.urlshorteningservice.entity.ShortUrl;
import com.hancidev.urlshorteningservice.repository.ShortUrlRepository;
import com.hancidev.urlshorteningservice.service.ShortUrlService;
import com.hancidev.urlshorteningservice.service.mapper.UrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository urlRepository;
    private final UrlMapper urlMapper;

    @Value("${app.shortener.domain}")
    private String domain;

    @Value("${app.shortener.code-length}")
    private int codeLength;

    @Override
    public ShortUrlResponse generateShortURL(CreateShortUrl createShortUrl) {
        Optional<ShortUrl> shortUrl = urlRepository.findShortUrlByOriginalUrl(createShortUrl.url());

        if (shortUrl.isPresent()) {
            return urlMapper.shortUrlResponseFromShortUrl(shortUrl.get());
        }

        String shortCode = generateUniqueShortCode(createShortUrl.url());
        ShortUrl url = urlMapper.shortUrlFromCreateShortUrl(createShortUrl);
        url.setShortCode(shortCode);
        urlRepository.save(url);

        return urlMapper.shortUrlResponseFromShortUrl(url);
    }

    @Override
    public ShortUrlResponse getOriginalUrl(String originalUrl) {
        Optional<ShortUrl> shortUrl = urlRepository.findShortUrlByOriginalUrl(originalUrl);

        if (shortUrl.isEmpty()) {
            throw new RuntimeException("No url such that");
        }

        shortUrl.get().setAccessCount(shortUrl.get().getAccessCount() + 1);
        urlRepository.save(shortUrl.get());

        return urlMapper.shortUrlResponseFromShortUrl(shortUrl.get());
    }

    @Override
    public ShortUrlResponse updateShortURL(CreateShortUrl createShortUrl, String shortCode) {
        Optional<ShortUrl> existingShortUrl = urlRepository.findShortUrlByShortCode(shortCode);

        if (existingShortUrl.isEmpty()) {
            return generateShortURL(createShortUrl);
        }

        ShortUrl shortUrl = existingShortUrl.get();
        shortUrl.setOriginalUrl(createShortUrl.url());
        shortUrl.setShortCode(generateUniqueShortCode(createShortUrl.url()));
        shortUrl.setUpdatedAt(LocalDateTime.now());

        urlRepository.save(shortUrl);

        return urlMapper.shortUrlResponseFromShortUrl(shortUrl);
    }

    @Override
    public void deleteURL(String shortCode) {
        ShortUrl shortUrl = urlRepository.findShortUrlByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("No such URL"));

        urlRepository.delete(shortUrl);
    }

    private String generateUniqueShortCode(String url) {
        String shortCode;
        boolean isUnique;
        int attempts = 0;

        do {
            shortCode = generateShortCode(url + domain);

            if (shortCode.length() > codeLength) {
                shortCode = shortCode.substring(0, codeLength);
            }

            isUnique = !urlRepository.existsByShortCode(shortCode);
            attempts++;

            if (attempts > 5 && !isUnique) {
                codeLength++;
                attempts = 0;
            }
        } while (!isUnique);

        return shortCode;
    }

    private String generateShortCode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
