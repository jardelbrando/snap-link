package com.snaplink.api.controller;

import com.snaplink.api.dto.response.UrlAnalyticsResponse;
import com.snaplink.api.exception.AccessDeniedException;
import com.snaplink.api.service.ClickLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ClickLogController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class ClickLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClickLogService clickLogService;

    @Test
    void shoudReturnMetricsWhenUserIsTheOwner() throws Exception {
        Long urlId = 1L;
        UUID userId = UUID.randomUUID();
        UrlAnalyticsResponse response = new UrlAnalyticsResponse(
                "https://site.com", "xyz", 150L, LocalDateTime.now()
        );

        when(clickLogService.getUrlMetrics(urlId, userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clicklog/urls/{urlId}", urlId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://site.com"))
                .andExpect(jsonPath("$.shortCode").value("xyz"))
                .andExpect(jsonPath("$.totalClicks").value(150L));

    }

    @Test
    void shouldReturnForbiddenWhenServiceThrowsAccessDeniedException() throws Exception {
        Long urlId = 1L;
        UUID userId = UUID.randomUUID();

        when(clickLogService.getUrlMetrics(urlId, userId))
                .thenThrow(new AccessDeniedException("You do not have permission to view the statistics for this link."));

        mockMvc.perform(get("/api/v1/clicklog/urls/{urlId}", urlId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Access Denied"))
                .andExpect(jsonPath("$.message").value("You do not have permission to view the statistics for this link."));
    }

}