package com.snaplink.api.service;

import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.exception.EmailAlreadyInUseException;
import com.snaplink.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shoudInsertUserSuccessfully() {

        UserCreateRequest request = new UserCreateRequest("test@test.com","password123");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("encryptedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.insert(request);

        assertNotNull(response);
        assertEquals(savedUser.getId(), response.getUserId());
        assertEquals("test@test.com", response.getEmail());

        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shoudThrowExceptionWhenAlreadyExists(){

        UserCreateRequest request = new UserCreateRequest("test@test.com","password123");
        User existingUser = new User();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyInUseException.class, () ->
            userService.insert(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldFindSuccessfully() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email("test@test.com")
                .password("password123")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.search(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("test@test.com", response.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getFullUserByEmail() {

        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        User user = User.builder()
                .id(userId)
                .email(email)
                .password("password123")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User fullUser = userService.getFullUserByEmail(email);

        assertNotNull(fullUser);
        assertEquals("test@test.com", fullUser.getEmail());
        assertEquals("password123", fullUser.getPassword());

        verify(userRepository, times(1)).findByEmail(email);
    }
}