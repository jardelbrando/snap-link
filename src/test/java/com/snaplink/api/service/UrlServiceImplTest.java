package com.snaplink.api.service;

import com.snaplink.api.domain.ClickLog;
import com.snaplink.api.domain.Url;
import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UrlShortenRequest;
import com.snaplink.api.dto.response.UrlResponse;
import com.snaplink.api.exception.ResourceNotFoundException;
import com.snaplink.api.exception.UserIdIsNullException;
import com.snaplink.api.repository.ClickLogRepository;
import com.snaplink.api.repository.UrlRepository;
import com.snaplink.api.repository.UserRepository;
import com.snaplink.api.util.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClickLogRepository clickLogRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private UrlServiceImpl urlService;


    @Test
    void shoudShortenUrl(){

        UUID userId = UUID.randomUUID();

        UrlShortenRequest urlShortenRequest = new UrlShortenRequest("test.com", userId);

        User user = User.builder()
                .id(userId)
                .email("email@test.com")
                .build();

        Url urlWithId = Url.builder()
                .id(1L)
                .originalUrl("test.com")
                .user(user)
                .build();

        Url urlWithShortCode = Url.builder()
                .id(1L)
                .originalUrl("test.com")
                .shortCode("b")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(urlRepository.save(any(Url.class))).thenReturn(urlWithId, urlWithShortCode);

        when(base62Encoder.encode(1L)).thenReturn("b");

        UrlResponse response = urlService.shortenUrl(urlShortenRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test.com", response.getOriginalUrl());
        assertEquals("b", response.getShortCode());
        assertNotNull(response.getCreatedAt());

        verify(userRepository, times(1)).findById(userId);
        verify(base62Encoder, times(1)).encode(1L);
        verify(urlRepository, times(2)).save(any(Url.class));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull(){

        UrlShortenRequest urlShortenRequest = new UrlShortenRequest("test.com", null);

        UserIdIsNullException exception = assertThrows(UserIdIsNullException.class, () ->
                urlService.shortenUrl(urlShortenRequest)
        );

        assertEquals("User Id is null", exception.getMessage());

        verifyNoInteractions(userRepository);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void shoudThrowExceptionWhenUserNotFound(){
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UrlShortenRequest urlShortenRequest = new UrlShortenRequest("test.com", userId);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                urlService.shortenUrl(urlShortenRequest)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void shouldReturnOriginalUrlAndLogClick(){
        String shortCode = "b";

        Url urlWithShortCode = Url.builder()
                .id(1L)
                .originalUrl("test.com")
                .shortCode("b")
                .createdAt(LocalDateTime.now())
                .build();

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(urlWithShortCode));

        String receivedOriginalUrl = urlService.getOriginalUrl(shortCode);

        assertEquals(urlWithShortCode.getOriginalUrl(), receivedOriginalUrl);

        verify(urlRepository, times(1)).findByShortCode(shortCode);
        verify(clickLogRepository, times(1)).save(any(ClickLog.class));
    }

    @Test
    void shouldThrowExceptionWhenShortCodeNotFound(){
        String shortCode = "b";

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                urlService.getOriginalUrl(shortCode)
        );

        assertEquals("Url not found", exception.getMessage());

        verify(urlRepository, times(1)).findByShortCode(shortCode);
        verifyNoInteractions(clickLogRepository);
    }
}