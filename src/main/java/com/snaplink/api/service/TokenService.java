package com.snaplink.api.service;

import com.snaplink.api.domain.User;

public interface TokenService {

    String generateToken(User user);

    String validateToken(String token);
}
