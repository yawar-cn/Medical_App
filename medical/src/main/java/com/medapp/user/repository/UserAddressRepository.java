package com.medapp.user.repository;

import com.medapp.user.entity.UserAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUserIdAndDeletedAtIsNull(UUID userId);

    Optional<UserAddress> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    Optional<UserAddress> findByUserIdAndIsDefaultAddressTrueAndDeletedAtIsNull(UUID userId);
}
