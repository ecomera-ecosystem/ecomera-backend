package com.youssef.ecomera.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import com.youssef.ecomera.user.enums.Role;


@Builder
public record RegisterRequest(
        @Schema(description = "User first name")
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstname,

        @Schema(description = "User last name")
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastname,

        @Schema(description = "Unique email address")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Password (hashed before storage)")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Schema(description = "Role assigned to the user")
        @NotNull(message = "Role is required")
        Role role
) {
}