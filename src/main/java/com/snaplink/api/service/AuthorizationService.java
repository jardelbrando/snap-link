package com.snaplink.api.service;

import com.snaplink.api.dto.request.LoginRequestDTO;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.TokenResponseDTO;
import com.snaplink.api.dto.response.UserResponse;

public interface AuthorizationService {

    TokenResponseDTO login(LoginRequestDTO data);

    UserResponse register(UserCreateRequest data);
}