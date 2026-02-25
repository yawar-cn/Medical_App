package com.medapp.auth.service;

import com.medapp.auth.domain.AuthDomain;
import com.medapp.auth.dto.LogoutRequest;
import com.medapp.auth.dto.OtpIssueResponse;
import com.medapp.auth.dto.OtpLoginRequest;
import com.medapp.auth.dto.OtpRequest;
import com.medapp.auth.dto.RefreshRequest;
import com.medapp.auth.dto.TokenResponse;
import com.medapp.auth.entity.OtpChallenge;
import com.medapp.auth.entity.RefreshToken;
import com.medapp.auth.exception.AuthException;
import com.medapp.auth.mapper.AuthMapper;
import com.medapp.common.constants.Role;
import com.medapp.common.logging.SensitiveDataMasker;
import com.medapp.config.AppProperties;
import com.medapp.user.entity.User;
import com.medapp.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthDomain authDomain;
    private final JwtTokenService jwtTokenService;
    private final PasswordService passwordService;
    private final AppProperties appProperties;
    private final UserRepository userRepository;

    public AuthService(AuthDomain authDomain,
                       JwtTokenService jwtTokenService,
                       PasswordService passwordService,
                       AppProperties appProperties,
                       UserRepository userRepository) {
        this.authDomain = authDomain;
        this.jwtTokenService = jwtTokenService;
        this.passwordService = passwordService;
        this.appProperties = appProperties;
        this.userRepository = userRepository;
    }

    @Transactional
    public OtpIssueResponse requestOtp(OtpRequest request) {
        String otp = generateOtp();
        OtpChallenge challenge = new OtpChallenge();
        challenge.setPhone(request.phone());
        challenge.setOtpHash(passwordService.hash(otp));
        challenge.setExpiresAt(Instant.now().plusSeconds(appProperties.getOtp().getTtlSeconds()));
        challenge.setAttempts(0);
        challenge.setConsumed(false);
        OtpChallenge saved = authDomain.saveChallenge(challenge);

        log.info("OTP generated for phone={} challengeId={} otp={}",
                SensitiveDataMasker.maskPhone(request.phone()),
                saved.getId(),
                appProperties.getOtp().isMockEnabled() ? otp : "suppressed");

        return new OtpIssueResponse(
                saved.getId(),
                SensitiveDataMasker.maskPhone(request.phone()),
                appProperties.getOtp().isMockEnabled() ? otp : null
        );
    }

    @Transactional
    public TokenResponse loginWithOtp(OtpLoginRequest request) {
        OtpChallenge challenge = authDomain.getActiveChallenge(request.challengeId(), request.phone());
        challenge.setAttempts(challenge.getAttempts() + 1);
        if (!passwordService.matches(request.otp(), challenge.getOtpHash())) {
            authDomain.saveChallenge(challenge);
            throw new AuthException("Invalid OTP", HttpStatus.UNAUTHORIZED);
        }

        challenge.setConsumed(true);
        authDomain.saveChallenge(challenge);

        Role role = request.role() == null ? Role.ROLE_USER : request.role();
        User user = userRepository.findByPhoneAndDeletedAtIsNull(request.phone()).orElseGet(() -> createUser(request.phone(), role));
        if (!user.isActive()) {
            throw new AuthException("User is inactive", HttpStatus.FORBIDDEN);
        }
        return issueTokenPair(user, "mobile");
    }

    @Transactional
    public TokenResponse refreshToken(RefreshRequest request) {
        Claims claims = jwtTokenService.parse(request.refreshToken());
        if (!jwtTokenService.isTokenType(claims, "refresh")) {
            throw new AuthException("Invalid token type", HttpStatus.UNAUTHORIZED);
        }
        String tokenHash = passwordService.sha256(request.refreshToken());
        RefreshToken refreshToken = authDomain.getValidRefreshToken(tokenHash);
        User user = authDomain.getUserFromRefreshToken(refreshToken);
        authDomain.revokeRefreshToken(refreshToken);
        return issueTokenPair(user, refreshToken.getDeviceInfo());
    }

    @Transactional
    public void logout(LogoutRequest request, String accessToken) {
        String refreshHash = passwordService.sha256(request.refreshToken());
        RefreshToken token = authDomain.getValidRefreshToken(refreshHash);
        authDomain.revokeRefreshToken(token);

        Claims accessClaims = jwtTokenService.parse(accessToken);
        authDomain.revokeAccessToken(jwtTokenService.jti(accessClaims), jwtTokenService.expiresAt(accessClaims));
    }

    public boolean isAccessTokenRevoked(String jti) {
        return authDomain.isAccessTokenRevoked(jti);
    }

    private TokenResponse issueTokenPair(User user, String deviceInfo) {
        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenService.generateRefreshToken(user.getId(), user.getRole());

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(passwordService.sha256(refreshToken));
        refreshTokenEntity.setExpiresAt(Instant.now().plusSeconds(jwtTokenService.refreshTtlSeconds()));
        refreshTokenEntity.setDeviceInfo(deviceInfo);
        refreshTokenEntity.setRevoked(false);
        authDomain.saveRefreshToken(refreshTokenEntity);

        return AuthMapper.toTokenResponse(
                accessToken,
                refreshToken,
                jwtTokenService.accessTtlSeconds(),
                jwtTokenService.refreshTtlSeconds()
        );
    }

    private User createUser(String phone, Role role) {
        User user = new User();
        user.setPhone(phone);
        user.setRole(role);
        user.setFullName("New User");
        user.setPasswordHash(passwordService.hash(UUID.randomUUID().toString()));
        user.setActive(true);
        return userRepository.save(user);
    }

    private String generateOtp() {
        int otp = new SecureRandom().nextInt(900000) + 100000;
        return Integer.toString(otp);
    }
}
