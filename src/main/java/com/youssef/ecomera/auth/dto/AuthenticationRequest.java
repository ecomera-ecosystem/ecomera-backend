package com.youssef.ecomera.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
public record AuthenticationRequest(
        @Schema(description = "User email address")
        String email,
        @Schema(description = "User password")
        String password
) {
}
