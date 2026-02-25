package com.medapp.auth.service;

import com.medapp.common.constants.Role;
import com.medapp.config.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-test-secret-key-123456");
        properties.setIssuer("test-issuer");
        properties.setAccessTokenTtlSeconds(900);
        properties.setRefreshTokenTtlSeconds(3600);
        jwtTokenService = new JwtTokenService(properties);
    }

    @Test
    void shouldGenerateAndParseAccessToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenService.generateAccessToken(userId, Role.ROLE_USER);
        Claims claims = jwtTokenService.parse(token);

        assertEquals(userId, jwtTokenService.userId(claims));
        assertEquals(Role.ROLE_USER, jwtTokenService.role(claims));
        assertTrue(jwtTokenService.isTokenType(claims, "access"));
    }

    @Test
    void shouldGenerateAndParseRefreshToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenService.generateRefreshToken(userId, Role.ROLE_USER);
        Claims claims = jwtTokenService.parse(token);

        assertEquals(userId, jwtTokenService.userId(claims));
        assertTrue(jwtTokenService.isTokenType(claims, "refresh"));
    }
}
