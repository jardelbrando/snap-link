package com.snaplink.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snaplink.api.dto.request.LoginRequestDTO;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.TokenResponseDTO;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.repository.UserRepository;
import com.snaplink.api.service.AuthorizationService;
import com.snaplink.api.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorizationService authService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Deve retornar 200 OK e o token JWT quando o login for válido")
    void login_WithValidRequest_ShouldReturnStatus200AndToken() throws Exception {
        var loginRequest = new LoginRequestDTO("jardel@snaplink.com", "123456");
        var tokenResponse = new TokenResponseDTO("eyJhbGciOiJIUzI1NiJ9...");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9..."));

        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Deve retornar 400 Bad Request se o JSON for inválido (validação @Valid)")
    void login_WithInvalidRequest_ShouldReturnStatus400() throws Exception {
        var invalidRequest = new LoginRequestDTO("email-invalido", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Deve retornar 201 Created e o UserResponse no cadastro com sucesso")
    void register_WithValidRequest_ShouldReturnStatus201AndUserResponse() throws Exception {
        var createRequest = new UserCreateRequest("jardel@snaplink.com", "minhasenha123");
        var userId = UUID.randomUUID();
        var userResponse = new UserResponse(userId, "jardel@snaplink.com", LocalDateTime.now());

        when(authService.register(any(UserCreateRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("jardel@snaplink.com"));

        verify(authService, times(1)).register(any(UserCreateRequest.class));
    }
}