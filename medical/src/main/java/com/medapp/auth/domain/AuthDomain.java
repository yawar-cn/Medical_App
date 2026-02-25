package com.medapp.auth.domain;

import com.medapp.auth.entity.OtpChallenge;
import com.medapp.auth.entity.RefreshToken;
import com.medapp.auth.entity.RevokedAccessToken;
import com.medapp.auth.exception.AuthException;
import com.medapp.auth.repository.OtpChallengeRepository;
import com.medapp.auth.repository.RefreshTokenRepository;
import com.medapp.auth.repository.RevokedAccessTokenRepository;
import com.medapp.common.exception.NotFoundException;
import com.medapp.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthDomain {

    private final OtpChallengeRepository otpChallengeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;

    public AuthDomain(OtpChallengeRepository otpChallengeRepository,
                      RefreshTokenRepository refreshTokenRepository,
                      RevokedAccessTokenRepository revokedAccessTokenRepository) {
        this.otpChallengeRepository = otpChallengeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
    }

    public OtpChallenge saveChallenge(OtpChallenge challenge) {
        return otpChallengeRepository.save(challenge);
    }

    public OtpChallenge getActiveChallenge(UUID challengeId, String phone) {
        OtpChallenge challenge = otpChallengeRepository.findByIdAndPhoneAndConsumedFalse(challengeId, phone)
                .orElseThrow(() -> new NotFoundException("OTP challenge not found"));
        if (challenge.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("OTP challenge expired", HttpStatus.UNAUTHORIZED);
        }
        if (challenge.getAttempts() >= 5) {
            throw new AuthException("Maximum OTP attempts exceeded", HttpStatus.TOO_MANY_REQUESTS);
        }
        return challenge;
    }

    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getValidRefreshToken(String tokenHash) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new AuthException("Invalid refresh token", HttpStatus.UNAUTHORIZED));
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Refresh token expired", HttpStatus.UNAUTHORIZED);
        }
        return refreshToken;
    }

    public void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    public boolean isAccessTokenRevoked(String jti) {
        return revokedAccessTokenRepository.findByJti(jti).isPresent();
    }

    public void revokeAccessToken(String jti, Instant expiresAt) {
        RevokedAccessToken revoked = new RevokedAccessToken();
        revoked.setJti(jti);
        revoked.setExpiresAt(expiresAt);
        revokedAccessTokenRepository.save(revoked);
    }

    public User getUserFromRefreshToken(RefreshToken token) {
        return token.getUser();
    }
}
