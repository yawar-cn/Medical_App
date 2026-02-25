package com.medapp.auth.repository;

import com.medapp.auth.entity.OtpChallenge;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {
    Optional<OtpChallenge> findByIdAndPhoneAndConsumedFalse(UUID id, String phone);
}
