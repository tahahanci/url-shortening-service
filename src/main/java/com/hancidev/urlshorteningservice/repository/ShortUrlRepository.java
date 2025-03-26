package com.hancidev.urlshorteningservice.repository;

import com.hancidev.urlshorteningservice.entity.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortUrlRepository extends MongoRepository<ShortUrl, String> {

    Optional<ShortUrl> findShortUrlByOriginalUrl(String originalUrl);

    boolean existsByShortCode(String shortCode);

    Optional<ShortUrl> findShortUrlByShortCode(String shortCode);
}
