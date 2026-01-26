package com.youssef.ecomera.auth.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import com.youssef.ecomera.user.enums.Role;


@Builder
public record RegisterRequest(
        @Schema(description = "User first name")
        String firstname,
        @Schema(description = "User last name")
        String lastname,
        @Schema(description = "Unique email address")
        String email,
        @Schema(description = "Password (hashed before storage)")
        String password,
        @Schema(description = "Role assigned to the user")
        Role role
) {
}