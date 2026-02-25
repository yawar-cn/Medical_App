package com.medapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Otp otp = new Otp();
    private final RateLimit rateLimit = new RateLimit();
    private final Cors cors = new Cors();

    public Otp getOtp() {
        return otp;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Otp {
        private boolean mockEnabled;
        private long ttlSeconds;

        public boolean isMockEnabled() {
            return mockEnabled;
        }

        public void setMockEnabled(boolean mockEnabled) {
            this.mockEnabled = mockEnabled;
        }

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }

    public static class RateLimit {
        private boolean enabled = true;
        private int requestsPerWindow;
        private long windowSeconds;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getRequestsPerWindow() {
            return requestsPerWindow;
        }

        public void setRequestsPerWindow(int requestsPerWindow) {
            this.requestsPerWindow = requestsPerWindow;
        }

        public long getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(long windowSeconds) {
            this.windowSeconds = windowSeconds;
        }
    }

    public static class Cors {
        private String[] allowedOrigins = new String[0];

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
