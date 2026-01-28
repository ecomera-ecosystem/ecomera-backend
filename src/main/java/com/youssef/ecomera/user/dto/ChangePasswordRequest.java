package com.youssef.ecomera.user.dto;

import lombok.Builder;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(name = "ChangePasswordRequest", description = "Request payload used to change the current user's password")
public record ChangePasswordRequest(

        @NotBlank(message = "Old password is required")
        @Schema(description = "Current password of the user", example = "OldP@ssw0rd")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        @Schema(description = "New password (minimum 8 characters)", example = "NewStrongP@ssw0rd")
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        @Schema(description = "Confirmation of the new password", example = "NewStrongP@ssw0rd")
        String confirmPassword
) {
}
