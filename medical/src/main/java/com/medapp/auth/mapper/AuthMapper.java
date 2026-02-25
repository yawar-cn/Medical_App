package com.medapp.auth.mapper;

import com.medapp.auth.dto.TokenResponse;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static TokenResponse toTokenResponse(String accessToken,
                                                String refreshToken,
                                                long accessExpiry,
                                                long refreshExpiry) {
        return new TokenResponse(accessToken, refreshToken, accessExpiry, refreshExpiry);
    }
}
