package com.hancidev.urlshorteningservice.service;

import com.hancidev.urlshorteningservice.dto.request.CreateShortUrl;
import com.hancidev.urlshorteningservice.dto.response.ShortUrlResponse;
import com.hancidev.urlshorteningservice.entity.ShortUrl;
import com.hancidev.urlshorteningservice.repository.ShortUrlRepository;
import com.hancidev.urlshorteningservice.service.impl.ShortUrlServiceImpl;
import com.hancidev.urlshorteningservice.service.mapper.UrlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortServiceTest {

    @Mock
    private ShortUrlRepository repository;

    @Mock
    private UrlMapper mapper;

    @InjectMocks
    private ShortUrlServiceImpl service;

    private CreateShortUrl createShortUrl;
    private ShortUrlResponse shortUrlResponse;
    private ShortUrl shortUrl;

    @BeforeEach
    void setUp() {
        createShortUrl = new CreateShortUrl("google.com");

        shortUrlResponse = ShortUrlResponse.builder()
                .urlID("A-12345")
                .shortCode("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        shortUrl = ShortUrl.builder()
                .id("A-12345")
                .urlID("A-12345")
                .shortCode("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .accessCount(1L)
                .build();
    }

    @Test
    void generateShortURL_shouldReturnShortUrlResponse_WhenUrlIsNew() {
        when(repository.findShortUrlByOriginalUrl(createShortUrl.url())).thenReturn(Optional.empty());
        when(mapper.shortUrlFromCreateShortUrl(createShortUrl)).thenReturn(shortUrl);
        when(mapper.shortUrlResponseFromShortUrl(any(ShortUrl.class))).thenReturn(shortUrlResponse);

        ShortUrlResponse response = service.generateShortURL(createShortUrl);

        assertThat(response).isNotNull();
        assertThat(response.urlID()).isEqualTo("A-12345");
        assertThat(response.shortCode()).isEqualTo("test");
        verify(repository).save(any(ShortUrl.class));
    }

    @Test
    void getOriginalURL_shouldReturnShortUrlResponse_WhenUrlExist() {
        when(repository.findShortUrlByOriginalUrl(createShortUrl.url())).thenReturn(Optional.of(shortUrl));
        when(mapper.shortUrlResponseFromShortUrl(shortUrl)).thenReturn(shortUrlResponse);

        ShortUrlResponse response = service.getOriginalUrl(createShortUrl.url());

        assertThat(response).isNotNull();
        assertThat(response.urlID()).isEqualTo(shortUrlResponse.urlID());
        assertThat(response.shortCode()).isEqualTo(shortUrlResponse.shortCode());

        verify(repository, times(1)).findShortUrlByOriginalUrl(createShortUrl.url());
        verify(repository).save(shortUrl);
    }

    @Test
    void getOriginalUrl_shouldThrowException_WhenUrlDoesNotExist() {
        when(repository.findShortUrlByOriginalUrl(createShortUrl.url())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOriginalUrl(createShortUrl.url()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No url such that");
    }

    @Test
    void updateShortURL_shouldReturnUpdatedShortUrlResponse_WhenShortCodeExists() {
        String shortCode = "test";
        CreateShortUrl updateRequest = new CreateShortUrl("updated-google.com");

        ShortUrl updatedShortUrl = ShortUrl.builder()
                .id("A-12345")
                .urlID("A-12345")
                .shortCode(shortCode)
                .originalUrl("updated-google.com")
                .createdAt(shortUrl.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .accessCount(shortUrl.getAccessCount())
                .build();

        ShortUrlResponse updatedResponse = ShortUrlResponse.builder()
                .urlID("A-12345")
                .shortCode(shortCode)
                .url("updated-google.com")
                .createdAt(shortUrl.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findShortUrlByShortCode(shortCode)).thenReturn(Optional.of(shortUrl));
        when(mapper.shortUrlResponseFromShortUrl(any(ShortUrl.class))).thenReturn(updatedResponse);
        when(repository.save(any(ShortUrl.class))).thenReturn(updatedShortUrl);

        ShortUrlResponse response = service.updateShortURL(updateRequest, shortCode);

        assertThat(response).isNotNull();
        assertThat(response.shortCode()).isEqualTo(shortCode);
        assertThat(response.url()).isEqualTo("updated-google.com");
        verify(repository).findShortUrlByShortCode(shortCode);
        verify(repository).save(any(ShortUrl.class));
    }

    @Test
    void deleteURL_shouldDeleteSuccessfully_WhenShortCodeExists() {
        String shortCode = "test";
        when(repository.findShortUrlByShortCode(shortCode)).thenReturn(Optional.of(shortUrl));

        service.deleteURL(shortCode);

        verify(repository).findShortUrlByShortCode(shortCode);
        verify(repository).delete(shortUrl);
    }

    @Test
    void deleteURL_shouldThrowException_WhenShortCodeDoesNotExist() {
        String shortCode = "nonexistent";
        when(repository.findShortUrlByShortCode(shortCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteURL(shortCode))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No such URL");

        verify(repository).findShortUrlByShortCode(shortCode);
        verify(repository, never()).delete(any(ShortUrl.class));
    }

    @Test
    void generateShortURL_shouldReturnExistingShortUrlResponse_WhenUrlAlreadyExists() {
        when(repository.findShortUrlByOriginalUrl(createShortUrl.url())).thenReturn(Optional.of(shortUrl));
        when(mapper.shortUrlResponseFromShortUrl(shortUrl)).thenReturn(shortUrlResponse);

        ShortUrlResponse response = service.generateShortURL(createShortUrl);

        assertThat(response).isNotNull();
        assertThat(response.urlID()).isEqualTo(shortUrlResponse.urlID());
        assertThat(response.shortCode()).isEqualTo(shortUrlResponse.shortCode());
        verify(repository).findShortUrlByOriginalUrl(createShortUrl.url());
        verify(repository, never()).save(any(ShortUrl.class));
    }

}
