package com.medapp.config.security;

import com.medapp.auth.service.AuthService;
import com.medapp.auth.service.JwtTokenService;
import com.medapp.user.entity.User;
import com.medapp.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                   AuthService authService,
                                   UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtTokenService.parse(token);
                if (!jwtTokenService.isTokenType(claims, "access")) {
                    unauthorized(response, "Invalid access token type");
                    return;
                }
                String jti = jwtTokenService.jti(claims);
                if (authService.isAccessTokenRevoked(jti)) {
                    unauthorized(response, "Access token revoked");
                    return;
                }

                UUID userId = jwtTokenService.userId(claims);
                User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                        .orElse(null);
                if (user == null) {
                    unauthorized(response, "User not found");
                    return;
                }
                if (!user.isActive()) {
                    unauthorized(response, "User inactive");
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        null,
                        List.of(new SimpleGrantedAuthority(user.getRole().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (JwtException ex) {
                SecurityContextHolder.clearContext();
                unauthorized(response, "Invalid or expired token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/actuator/health")
                || path.startsWith("/api/v1/payments/webhook");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
