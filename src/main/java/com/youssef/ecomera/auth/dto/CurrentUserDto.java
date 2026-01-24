package com.youssef.ecomera.auth.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
public record CurrentUserDto(
        @Schema(description = "User ID") String id,
        @Schema(description = "User email") String email,
        @Schema(description = "First name") String firstname,
        @Schema(description = "Last name") String lastname,
        @Schema(description = "Role") String role,
        @Schema(description = "Last login timestamp") String lastLogin,
        @Schema(description = "Last known IP address") String ipAddress
) {}

