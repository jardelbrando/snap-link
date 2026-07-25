package com.snaplink.api.controller;

import com.snaplink.api.dto.request.LoginRequestDTO;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.TokenResponseDTO;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.service.AuthorizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthorizationService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var response = authService.login(data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserCreateRequest data) {
        var response = authService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
