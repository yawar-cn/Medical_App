package com.medapp.delivery.repository;

import com.medapp.delivery.entity.RiderProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderProfileRepository extends JpaRepository<RiderProfile, UUID> {
    Optional<RiderProfile> findByUserId(UUID userId);

    List<RiderProfile> findByAvailableTrue();
}
