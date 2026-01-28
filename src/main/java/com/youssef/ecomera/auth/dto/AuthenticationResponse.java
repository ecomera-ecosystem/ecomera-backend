package com.youssef.ecomera.auth.dto;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
public record AuthenticationResponse(
        @JsonProperty("access_token")
        @Schema(description = "JWT access token")
        String accessToken,
        @JsonProperty("refresh_token")
        @Schema(description = "JWT refresh token")
        String refreshToken
) {
}
