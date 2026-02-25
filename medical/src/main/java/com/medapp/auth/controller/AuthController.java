package com.medapp.auth.controller;

import com.medapp.auth.dto.LogoutRequest;
import com.medapp.auth.dto.OtpIssueResponse;
import com.medapp.auth.dto.OtpLoginRequest;
import com.medapp.auth.dto.OtpRequest;
import com.medapp.auth.dto.RefreshRequest;
import com.medapp.auth.dto.TokenResponse;
import com.medapp.auth.service.AuthService;
import com.medapp.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/otp/request")
    public ApiResponse<OtpIssueResponse> requestOtp(@RequestBody @Valid OtpRequest request) {
        return ApiResponse.ok("OTP issued", authService.requestOtp(request));
    }

    @PostMapping("/otp/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid OtpLoginRequest request) {
        return ApiResponse.ok("Login successful", authService.loginWithOtp(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        return ApiResponse.ok("Token refreshed", authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request,
                                    @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        authService.logout(request, accessToken);
        return ApiResponse.ok("Logged out", null);
    }
}
