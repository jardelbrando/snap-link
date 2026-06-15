package com.snaplink.api.service;

import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse insert(UserCreateRequest userCreateRequest);

    UserResponse search(UUID uuid);

    User getFullUserByEmail(String email);
}
