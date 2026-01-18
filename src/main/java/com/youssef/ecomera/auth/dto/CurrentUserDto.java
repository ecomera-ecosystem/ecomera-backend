package com.youssef.ecomera.auth.dto;

import lombok.Builder;

@Builder
public record CurrentUserDto(
        String id,
        String email,
        String firstname,
        String lastname,
        String role,
        String lastLogin,
        String ipAddress
) {
}

