package com.snaplink.api.service;

import com.snaplink.api.domain.Url;
import com.snaplink.api.dto.response.UrlAnalyticsResponse;
import com.snaplink.api.dto.response.UserDashboardResponse;
import com.snaplink.api.exception.AccessDeniedException;
import com.snaplink.api.exception.ResourceNotFoundException;
import com.snaplink.api.repository.ClickLogRepository;
import com.snaplink.api.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClickLogServiceImpl implements ClickLogService {

    private final ClickLogRepository clickLogRepository;

    private final UrlRepository urlRepository;

    @Override
    @Transactional(readOnly = true)
    public UrlAnalyticsResponse getUrlMetrics(Long urlId, UUID userId) {

        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new ResourceNotFoundException("Url Not Found"));

        if (url.getUser() == null || !url.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to view the statistics for this link.");
        }

        long totalClicks = clickLogRepository.countByUrlId(urlId);

        return new UrlAnalyticsResponse(
                url.getOriginalUrl(),
                url.getShortCode(),
                totalClicks,
                url.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserDashboardResponse getUserDashboardMetrics(UUID userId) {

        long totalLinks = urlRepository.countByUserId(userId);

        long totalClicks = clickLogRepository.countByUrlUserId(userId);

        return new UserDashboardResponse(totalLinks, totalClicks);
    }
}
