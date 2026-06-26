package com.snaplink.api.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlShortenRequest {

    private String originalUrl;
    private UUID userId;
}
