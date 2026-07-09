package com.snaplink.api.service;

import com.snaplink.api.domain.User;
import com.snaplink.api.dto.request.LoginRequestDTO;
import com.snaplink.api.dto.request.UserCreateRequest;
import com.snaplink.api.dto.response.TokenResponseDTO;
import com.snaplink.api.dto.response.UserResponse;
import com.snaplink.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @Test
    @DisplayName("login: It must authenticate the user and successfully return a JWT.")
    void login_WithValidCredentials_ShouldReturnTokenResponseDTO() {
        var loginRequest = new LoginRequestDTO("jardel@snaplink.com", "123456");
        var mockUser = User.builder()
                .id(UUID.randomUUID())
                .email("jardel@snaplink.com")
                .password("hashedPassword")
                .build();

        Authentication mockAuth = mock(Authentication.class);

        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(tokenService.generateToken(mockUser)).thenReturn("jwt.token.valido");

        TokenResponseDTO result = authorizationService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo("jwt.token.valido");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(mockUser);
    }

    @Test
    @DisplayName("login: It must propagate an exception when the AuthenticationManager fails due to an incorrect password.")
    void login_WithInvalidCredentials_ShouldThrowException() {
        var loginRequest = new LoginRequestDTO("jardel@snaplink.com", "senhaEerrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authorizationService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verifyNoInteractions(tokenService);
    }

    @Test
    @DisplayName("register: You should delegate the creation to the UserService and return a UserResponse.")
    void register_WithValidData_ShouldReturnUserResponse() {
        var createRequest = new UserCreateRequest("jardel@snaplink.com", "123456");
        var expectedResponse = new UserResponse(UUID.randomUUID(), "jardel@snaplink.com", LocalDateTime.now());

        when(userService.insert(createRequest)).thenReturn(expectedResponse);

        UserResponse result = authorizationService.register(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(expectedResponse.getUserId());
        assertThat(result.getEmail()).isEqualTo("jardel@snaplink.com");

        verify(userService, times(1)).insert(createRequest);
    }

    @Test
    @DisplayName("loadUserByUsername: It should return UserDetails when the email exists in the database.")
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        String email = "jardel@snaplink.com";
        var mockUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails result = authorizationService.loadUserByUsername(email);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(email);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("loadUserByUsername: It should throw UsernameNotFoundException when the email does not exist.")
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        String email = "fantasma@snaplink.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorizationService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User Not Found!");

        verify(userRepository, times(1)).findByEmail(email);
    }
}