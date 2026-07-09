package com.snaplink.api.service;

import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.LoginRequestDTO;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.TokenResponseDTO;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService, UserDetailsService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    @Override
    public TokenResponseDTO login(LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var user = (User) auth.getPrincipal();
        String token = tokenService.generateToken(user);

        return new TokenResponseDTO(token);
    }

    @Override
    public UserResponse register(UserCreateRequest data) {
        return userService.insert(data);
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found!"));
    }
}