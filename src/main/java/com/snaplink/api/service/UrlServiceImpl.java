package com.snaplink.api.service;

import com.snaplink.api.domain.ClickLog;
import com.snaplink.api.domain.Url;
import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UrlShortenRequest;
import com.snaplink.api.dto.response.UrlResponse;
import com.snaplink.api.exception.ResourceNotFoundException;
import com.snaplink.api.repository.ClickLogRepository;
import com.snaplink.api.repository.UrlRepository;
import com.snaplink.api.repository.UserRepository;
import com.snaplink.api.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final ClickLogRepository clickLogRepository;
    private final Base62Encoder base62Encoder;


    @Override
    @Transactional
    public UrlResponse shortenUrl(UrlShortenRequest request) {

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode("TEMP")
                .user(resolveUser(request.getUserId()))
                .build();

        urlRepository.save(url);

        String shortCode = base62Encoder.encode(url.getId());
        url.setShortCode(shortCode);

        return UrlResponse.fromEntity(url);
    }

    @Override
    @Transactional
    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Url not found"));

        ClickLog log = ClickLog.builder()
                .url(url)
                .build();

        clickLogRepository.save(log);

        return url.getOriginalUrl();
    }

    private User resolveUser(UUID userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
