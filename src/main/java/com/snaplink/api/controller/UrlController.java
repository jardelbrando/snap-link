package com.snaplink.api.controller;

import com.snaplink.api.dto.request.UrlShortenRequest;
import com.snaplink.api.dto.response.UrlResponse;
import com.snaplink.api.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody @Valid UrlShortenRequest request){
        UrlResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode){
        String originalUrl = urlService.getOriginalUrl(shortCode);

        if(!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")){
            originalUrl = "https://" + originalUrl;
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }
}
