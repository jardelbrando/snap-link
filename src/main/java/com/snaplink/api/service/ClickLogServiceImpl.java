package com.snaplink.api.service;

import com.snaplink.api.domain.ClickLog;
import com.snaplink.api.domain.Url;
import com.snaplink.api.dto.response.UrlAnalyticsResponse;
import com.snaplink.api.dto.response.UserDashboardResponse;
import com.snaplink.api.exception.AccessDeniedException;
import com.snaplink.api.exception.ResourceNotFoundException;
import com.snaplink.api.repository.ClickLogRepository;
import com.snaplink.api.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClickLogServiceImpl implements ClickLogService {

    private final ClickLogRepository clickLogRepository;

    private final UrlRepository urlRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public UrlAnalyticsResponse getUrlMetrics(Long urlId, UUID userId) {

        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new ResourceNotFoundException("Url not found"));

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

    @Override
    @Transactional
    public void registerClickAndNotify(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Url not found"));

        ClickLog click = new ClickLog();
        click.setUrl(url);
        click.setAccessDate(LocalDateTime.now());
        clickLogRepository.save(click);

        long currentUrlClicks = clickLogRepository.countByUrlId(url.getId());
        UrlAnalyticsResponse urlAnalytics = new UrlAnalyticsResponse(
                url.getOriginalUrl(),
                url.getShortCode(),
                currentUrlClicks,
                url.getCreatedAt()
        );
        messagingTemplate.convertAndSend("/topic/analytics/" + url.getId(), urlAnalytics);

        if (url.getUser() != null) {
            UUID userId = url.getUser().getId();

            long totalLinks = urlRepository.countByUserId(userId);
            long totalUserClicks = clickLogRepository.countByUrlUserId(userId);

            UserDashboardResponse dashboardMetrics = new UserDashboardResponse(totalLinks, totalUserClicks);

            messagingTemplate.convertAndSend("/topic/dashboard/" + userId, dashboardMetrics);
        }
    }
}
