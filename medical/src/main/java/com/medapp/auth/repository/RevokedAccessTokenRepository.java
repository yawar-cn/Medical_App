package com.medapp.auth.repository;

import com.medapp.auth.entity.RevokedAccessToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, UUID> {
    Optional<RevokedAccessToken> findByJti(String jti);

    void deleteByExpiresAtBefore(Instant cutoff);
}
