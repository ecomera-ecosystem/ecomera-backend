package com.youssef.ecomera.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines fine-grained permissions for role-based access control.
 * Convention: domain:action (e.g., "admin:create", "management:read").
 */
@Getter
@RequiredArgsConstructor
@Schema(name = "Permission", description = "Fine-grained permissions following the domain:action convention")
public enum Permission {

    @Schema(description = "Create admin resources")
    ADMIN_CREATE("admin:create"),

    @Schema(description = "Read admin resources")
    ADMIN_READ("admin:read"),

    @Schema(description = "Update admin resources")
    ADMIN_UPDATE("admin:update"),

    @Schema(description = "Delete admin resources")
    ADMIN_DELETE("admin:delete"),

    @Schema(description = "Create management resources")
    MANAGER_CREATE("management:create"),

    @Schema(description = "Read management resources")
    MANAGER_READ("management:read"),

    @Schema(description = "Update management resources")
    MANAGER_UPDATE("management:update"),

    @Schema(description = "Delete management resources")
    MANAGER_DELETE("management:delete");

    @JsonValue
    private final String value;
}