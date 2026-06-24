package com.snaplink.api.controller;

import com.snaplink.api.dto.request.UrlShortenRequest;
import com.snaplink.api.dto.response.UrlResponse;
import com.snaplink.api.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(controllers = UrlController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    void shoudShortenUrlSuccessfully() throws Exception {

        UrlShortenRequest request = UrlShortenRequest.builder()
                .originalUrl("https://site.com")
                .userId(UUID.randomUUID())
                .build();

        UrlResponse response = new UrlResponse(1L, "https://site.com", "xyz", LocalDateTime.now());

        when(urlService.shortenUrl(any(UrlShortenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalUrl").value("https://site.com"))
                .andExpect(jsonPath("$.shortCode").value("xyz"));
    }

    @Test
    void shouldRedirectToOriginalUrlSuccessfully() throws Exception {
        String shortCode = "xyz";
        String targetUrl = "https://site.com";

        when(urlService.getOriginalUrl(shortCode)).thenReturn(targetUrl);

        mockMvc.perform(get("/api/v1/url/{shortCode}", shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, targetUrl));
    }
}