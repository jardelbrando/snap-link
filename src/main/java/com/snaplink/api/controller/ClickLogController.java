package com.snaplink.api.controller;

import com.snaplink.api.dto.response.UrlAnalyticsResponse;
import com.snaplink.api.dto.response.UserDashboardResponse;
import com.snaplink.api.service.ClickLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clicklog")
public class ClickLogController {

    private final ClickLogService clickLogService;

    @GetMapping("/urls/{urlId}")
    public ResponseEntity<UrlAnalyticsResponse> getUrlMetrics(
            @PathVariable Long urlId,
            @RequestHeader("X-User-Id")UUID userId){

        UrlAnalyticsResponse response = clickLogService.getUrlMetrics(urlId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardResponse> getUserDashboardMetrics(
            @RequestHeader("X-User-Id") UUID userId){

        UserDashboardResponse response = clickLogService.getUserDashboardMetrics(userId);
        return ResponseEntity.ok(response);
    }
}
