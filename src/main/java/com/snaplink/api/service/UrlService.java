package com.snaplink.api.service;

import com.snaplink.api.dto.request.UrlShortenRequest;
import com.snaplink.api.dto.response.UrlResponse;

public interface UrlService {

    UrlResponse shortenUrl(UrlShortenRequest request);

    String getOriginalUrl(String shortCode);
}
