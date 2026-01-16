package com.youssef.ecomera.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines fine-grained permissions for role-based access control.
 * Convention: domain:action (e.g., "admin:create", "management:read").
 */
@Getter
@RequiredArgsConstructor
public enum Permission {

    // Admin permissions
    ADMIN_CREATE("admin:create"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),

    // Manager permissions
    MANAGER_CREATE("management:create"),
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_DELETE("management:delete");

    private final String value;

    @Override
    public String toString() {
        return this.value;
    }
}
