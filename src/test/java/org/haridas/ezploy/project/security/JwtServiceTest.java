package org.haridas.ezploy.project.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtServiceTest {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expirationTime);
    }

    @Test
    void shouldGenerateToken() {

        String token = jwtService.generateToken("username");
        SecretKey secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(token).isNotBlank();
        assertThat(claims.getSubject()).isEqualTo("username");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken("username");
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("username");
    }
}