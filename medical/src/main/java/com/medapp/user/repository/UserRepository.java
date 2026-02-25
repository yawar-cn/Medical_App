package com.medapp.user.repository;

import com.medapp.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneAndDeletedAtIsNull(String phone);

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByPhone(String phone);
}
