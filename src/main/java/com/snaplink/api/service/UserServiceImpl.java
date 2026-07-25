package com.snaplink.api.service;

import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.exception.EmailAlreadyInUseException;
import com.snaplink.api.exception.ResourceNotFoundException;
import com.snaplink.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse insert(UserCreateRequest userCreateRequest) {

        if(userRepository.findByEmail(userCreateRequest.email()).isPresent())
            throw new EmailAlreadyInUseException("The email address is already in use");

        String hashedPassword = passwordEncoder.encode(userCreateRequest.password());

        User user = User.builder()
                .email(userCreateRequest.email())
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse search(UUID uuid) {

        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User getFullUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
