package com.snaplink.api.service;

import com.snaplink.api.dto.response.UrlAnalyticsResponse;
import com.snaplink.api.dto.response.UserDashboardResponse;

import java.util.UUID;

public interface ClickLogService {

    UrlAnalyticsResponse getUrlMetrics(Long urlId, UUID userId);

    UserDashboardResponse getUserDashboardMetrics(UUID userId);
}
