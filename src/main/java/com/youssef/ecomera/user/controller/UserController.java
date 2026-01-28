package com.youssef.ecomera.user.controller;

import com.youssef.ecomera.user.service.UserService;
import com.youssef.ecomera.user.dto.ChangePasswordRequest;
import com.youssef.ecomera.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and account management APIs")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Change current user's password", description = "Allows an authenticated user to change their password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            @Parameter(hidden = true) Principal connectedUser
    ) {
        userService.changePassword(req, connectedUser);
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get connected user profile")
    @GetMapping("/me")
    public ResponseEntity<User> getConnectedUser(Principal connectedUser) {
        return ResponseEntity.ok(userService.getConnectedUser(connectedUser));
    }

    @Operation(summary = "Get connected user roles")
    @GetMapping("/me/roles")
    public ResponseEntity<List<String>> getConnectedUserRoles(Principal connectedUser) {
        return ResponseEntity.ok(userService.getConnectedUserRoles(connectedUser));
    }
}