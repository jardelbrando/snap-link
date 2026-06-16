package com.snaplink.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlAnalyticsResponse {

    private String originalUrl;
    private String shortCode;
    private long totalClicks;
    private LocalDateTime createdAt;
}
