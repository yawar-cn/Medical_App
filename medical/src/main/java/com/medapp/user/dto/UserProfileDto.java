package com.medapp.user.dto;

import com.medapp.common.constants.Role;
import java.util.UUID;

public record UserProfileDto(
        UUID id,
        String phone,
        String email,
        String fullName,
        Role role
) {
}
