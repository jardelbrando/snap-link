package com.snaplink.api.dto.response;

import com.snaplink.api.domain.Url;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponse {

    private long id;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdAt;


    public static UrlResponse fromEntity(Url url) {
        return new UrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                url.getCreatedAt()
        );
    }
}
