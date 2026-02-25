package com.medapp.auth.repository;

import com.medapp.auth.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    void deleteByExpiresAtBefore(Instant cutoff);
}
