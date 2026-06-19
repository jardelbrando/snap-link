package com.snaplink.api.service;

import com.snaplink.api.domain.Url;
import com.snaplink.api.domain.User;
import com.snaplink.api.dto.response.UrlAnalyticsResponse;
import com.snaplink.api.dto.response.UserDashboardResponse;
import com.snaplink.api.exception.AccessDeniedException;
import com.snaplink.api.exception.ResourceNotFoundException;
import com.snaplink.api.repository.ClickLogRepository;
import com.snaplink.api.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClickLogServiceImplTest {

    @Mock
    private ClickLogRepository clickLogRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private ClickLogServiceImpl clickLogService;

    @Test
    void shouldReturnUrlMetricsSuccessfully() {
        UUID userId = UUID.randomUUID();
        Long urlId = 1L;

        User user = User.builder()
                .id(userId)
                .email("test@test.com")
                .build();

        LocalDateTime dataCriacao = LocalDateTime.now();
        Url url = Url.builder()
                .id(urlId)
                .originalUrl("test.com")
                .shortCode("xyz")
                .createdAt(dataCriacao)
                .user(user)
                .build();

        when(urlRepository.findById(urlId)).thenReturn(Optional.of(url));

        when(clickLogRepository.countByUrlId(urlId)).thenReturn(150L);

        UrlAnalyticsResponse response = clickLogService.getUrlMetrics(urlId, userId);

        assertNotNull(response);
        assertEquals("test.com", response.getOriginalUrl());
        assertEquals("xyz", response.getShortCode());
        assertEquals(150L, response.getTotalClicks());
        assertEquals(dataCriacao, response.getCreatedAt());

        verify(urlRepository, times(1)).findById(urlId);
        verify(clickLogRepository, times(1)).countByUrlId(urlId);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUrlDoesNotExist(){
        UUID userId = UUID.randomUUID();
        Long urlId = 1L;

        when(urlRepository.findById(urlId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                clickLogService.getUrlMetrics(urlId, userId)
        );

        assertEquals("Url not found", exception.getMessage());

        verifyNoInteractions(clickLogRepository);
        verify(urlRepository, times(1)).findById(urlId);
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUrlIsAnonymous(){
        UUID userId = UUID.randomUUID();
        Long urlId = 1L;

        Url url = Url.builder()
                .id(urlId)
                .originalUrl("test@test.com")
                .shortCode("b")
                .build();

        when(urlRepository.findById(urlId)).thenReturn(Optional.of(url));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            clickLogService.getUrlMetrics(urlId, userId)
        );

        assertEquals("You do not have permission to view the statistics for this link.", exception.getMessage());

        verifyNoInteractions(clickLogRepository);
        verify(urlRepository, times(1)).findById(urlId);
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUrlBelongsToAnotherUser(){
        UUID userId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        Long urlId = 1L;

        User user = User.builder()
                .id(anotherUserId)
                .email("test@test.com")
                .build();

        Url url = Url.builder()
                .id(urlId)
                .user(user)
                .originalUrl("test@test.com")
                .shortCode("b")
                .build();

        when(urlRepository.findById(urlId)).thenReturn(Optional.of(url));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                clickLogService.getUrlMetrics(urlId, userId)
        );

        assertEquals("You do not have permission to view the statistics for this link.", exception.getMessage());

        verifyNoInteractions(clickLogRepository);
        verify(urlRepository, times(1)).findById(urlId);

    }

    @Test
    void shouldReturnUserDashboardMetricsSuccessfully(){

        UUID userId = UUID.randomUUID();

        when(urlRepository.countByUserId(userId)).thenReturn(8L);

        when(clickLogRepository.countByUrlUserId(userId)).thenReturn(1200L);

        UserDashboardResponse response = clickLogService.getUserDashboardMetrics(userId);

        assertNotNull(response);
        assertEquals(8L, response.getTotalLinksCreated());
        assertEquals(1200L, response.getTotalClicksAccumulated());

        verify(urlRepository, times(1)).countByUserId(userId);
        verify(clickLogRepository, times(1)).countByUrlUserId(userId);
    }
}