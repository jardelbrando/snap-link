package com.snaplink.api.service;

import com.snaplink.api.domain.ClickLog;
import com.snaplink.api.domain.Url;
import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UrlShortenRequest;
import com.snaplink.api.dto.response.UrlResponse;
import com.snaplink.api.exception.UrlNotFoundException;
import com.snaplink.api.exception.UserNotFoundException;
import com.snaplink.api.repository.ClickLogRepository;
import com.snaplink.api.repository.UrlRepository;
import com.snaplink.api.repository.UserRepository;
import com.snaplink.api.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        User user = null;

        if(request.getUserId() != null){
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not Found"));
        }

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .user(user)
                .build();

        url = urlRepository.save(url);

        String shortCode = base62Encoder.encode(url.getId());

        url.setShortCode(shortCode);
        Url savedUrl = urlRepository.save(url);

        return new UrlResponse(
                savedUrl.getId(),
                savedUrl.getOriginalUrl(),
                savedUrl.getShortCode(),
                savedUrl.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found"));

        ClickLog log = ClickLog.builder()
                .url(url)
                .build();

        clickLogRepository.save(log);

        return url.getOriginalUrl();
    }
}
