package com.medapp.auth.service;

import com.medapp.common.constants.Role;
import com.medapp.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(UUID userId, Role role) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenTtlSeconds());
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .id(UUID.randomUUID().toString())
                .claims(Map.of("role", role.name(), "type", "access"))
                .signWith(signingKey())
                .compact();
    }

    public String generateRefreshToken(UUID userId, Role role) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getRefreshTokenTtlSeconds());
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .id(UUID.randomUUID().toString())
                .claims(Map.of("role", role.name(), "type", "refresh"))
                .signWith(signingKey())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenType(Claims claims, String expectedType) {
        return expectedType.equals(claims.get("type", String.class));
    }

    public UUID userId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public Role role(Claims claims) {
        return Role.valueOf(claims.get("role", String.class));
    }

    public String jti(Claims claims) {
        return claims.getId();
    }

    public Instant expiresAt(Claims claims) {
        return claims.getExpiration().toInstant();
    }

    public long accessTtlSeconds() {
        return jwtProperties.getAccessTokenTtlSeconds();
    }

    public long refreshTtlSeconds() {
        return jwtProperties.getRefreshTokenTtlSeconds();
    }

    private SecretKey signingKey() {
        byte[] secret = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secret);
    }
}
