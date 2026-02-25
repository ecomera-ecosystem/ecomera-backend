package com.youssef.ecomera.user.controller;

import com.youssef.ecomera.user.dto.UserDto;
import com.youssef.ecomera.user.service.UserService;
import com.youssef.ecomera.user.dto.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Users", description = "User profile and account management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ')")
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get connected user profile")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    public ResponseEntity<UserDto> getConnectedUser(Principal connectedUser) {
        return ResponseEntity.ok(userService.getConnectedUser(connectedUser));
    }

    @GetMapping("/me/roles")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get connected user roles")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    public ResponseEntity<List<String>> getConnectedUserRoles(Principal connectedUser) {
        return ResponseEntity.ok(userService.getConnectedUserRoles(connectedUser));
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Change current user's password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid password data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            @Parameter(hidden = true) Principal connectedUser) {
        userService.changePassword(req, connectedUser);
        return ResponseEntity.ok("Password changed successfully");
    }
}