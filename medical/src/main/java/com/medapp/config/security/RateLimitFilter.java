package com.medapp.config.security;

import com.medapp.common.exception.ErrorCode;
import com.medapp.common.exception.ErrorResponse;
import com.medapp.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(StringRedisTemplate redisTemplate, AppProperties appProperties, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!appProperties.getRateLimit().isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
        String route = request.getRequestURI();
        String key = "ratelimit:" + ip + ":" + route;

        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, appProperties.getRateLimit().getWindowSeconds(), TimeUnit.SECONDS);
        }

        if (count != null && count > appProperties.getRateLimit().getRequestsPerWindow()) {
            response.setStatus(429);
            response.setContentType("application/json");
            ErrorResponse error = new ErrorResponse(
                    Instant.now(),
                    429,
                    ErrorCode.RATE_LIMITED.name(),
                    "Too many requests",
                    List.of(),
                    request.getRequestURI()
            );
            response.getWriter().write(objectMapper.writeValueAsString(error));
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }
}
