package com.youssef.ecomera.user.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines application roles and their associated permissions.
 * Each role maps to both ROLE_* authorities and fine-grained permissions.
 */
@Schema(description = "User role with hierarchical permissions for role-based access control")
@Getter
@RequiredArgsConstructor
public enum Role {

    @Schema(description = "Standard user with basic access to user endpoints")
    USER(Set.of()),

    @Schema(description = "Manager with full access to management endpoints (create, read, update, delete)")
    MANAGER(Set.of(
            Permission.MANAGER_CREATE,
            Permission.MANAGER_READ,
            Permission.MANAGER_UPDATE,
            Permission.MANAGER_DELETE
    )),

    @Schema(description = "Administrator with full system access (all admin and manager permissions)")
    ADMIN(Set.of(
            Permission.ADMIN_CREATE,
            Permission.ADMIN_READ,
            Permission.ADMIN_UPDATE,
            Permission.ADMIN_DELETE,
            Permission.MANAGER_CREATE,
            Permission.MANAGER_READ,
            Permission.MANAGER_UPDATE,
            Permission.MANAGER_DELETE
    ));

    @JsonIgnore  // Internal implementation detail, not exposed in API
    private final Set<Permission> permissions;

    /**
     * Converts role + permissions into Spring Security authorities.
     * Used internally by Spring Security for authorization checks.
     *
     * @return List of granted authorities including role and permissions
     */
    @JsonIgnore  // Internal method, not serialized
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.toString()))
                .collect(Collectors.toList());

        // Add ROLE_* authority for role-based checks
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}