package org.haridas.ezploy.project.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "your-test-secret-key-that-is-at-least-32-characters-long";

    private static final long EXPIRATION_TIME = 3600000;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_TIME);
    }

    @Test
    void shouldGenerateToken() {

        String token = jwtService.generateToken("username");

        SecretKey secretKey = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
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