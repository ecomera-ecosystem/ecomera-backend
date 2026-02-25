package com.youssef.ecomera.user.dto;

import com.youssef.ecomera.user.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        LocalDateTime lastLogin,
        String ipAddress,
        LocalDateTime createdAt
) {
}
