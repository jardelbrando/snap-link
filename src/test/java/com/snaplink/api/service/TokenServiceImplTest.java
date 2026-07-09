package com.snaplink.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.snaplink.api.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    private User user;
    private static final String SECRET_TEST = "c8f94e2a71b30d658c9e4a12b7f03d865e91a2c4f8d60b371a5c9e2d8b4f0a63";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_TEST);

        user = User.builder()
                .id(UUID.randomUUID())
                .email("jardel.brandao@snaplink.com")
                .password("senha_criptografada_123")
                .build();
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido quando o usuário for informado")
    void generateToken_WhenUserIsValid_ShouldReturnJwtToken() {
        String token = tokenService.generateToken(user);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Deve validar o token e retornar o email (subject) quando o token for válido")
    void validateToken_WhenTokenIsValid_ShouldReturnSubjectEmail() {
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertThat(subject).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Deve retornar string vazia quando o token for inválido ou adulterado")
    void validateToken_WhenTokenIsInvalidOrTampered_ShouldReturnEmptyString() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.payload";

        String subject = tokenService.validateToken(invalidToken);

        assertThat(subject).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar string vazia quando o token estiver expirado")
    void validateToken_WhenTokenIsExpired_ShouldReturnEmptyString() {
        Instant expiredDate = LocalDateTime.now().minusHours(1).toInstant(ZoneOffset.of("-03:00"));

        String expiredToken = JWT.create()
                .withIssuer("snaplink-api")
                .withSubject(user.getEmail())
                .withExpiresAt(expiredDate)
                .sign(Algorithm.HMAC256(SECRET_TEST));

        String subject = tokenService.validateToken(expiredToken);

        assertThat(subject).isEmpty();
    }
}