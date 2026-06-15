package com.snaplink.api.service;

import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.exception.EmailAlreadyInUseException;
import com.snaplink.api.exception.UserNotFoundException;
import com.snaplink.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse insert(UserCreateRequest userCreateRequest) {

        if(userRepository.findByEmail(userCreateRequest.getEmail()).isPresent())
            throw new EmailAlreadyInUseException("The email address is already in use");

        String hashedPassword = passwordEncoder.encode(userCreateRequest.getPassword());

        User user = User.builder()
                .email(userCreateRequest.getEmail())
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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    @Override
    public User getFullUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
