package com.youssef.ecomera.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.youssef.ecomera.auth.dto.AuthenticationRequest;
import com.youssef.ecomera.auth.dto.AuthenticationResponse;
import com.youssef.ecomera.auth.dto.CurrentUserDto;
import com.youssef.ecomera.auth.service.AuthenticationService;
import com.youssef.ecomera.auth.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints for user registration, login, and token management")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with email, password, name, and role. Returns JWT access and refresh tokens upon successful registration."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Registration successful - Returns JWT tokens",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input data - Validation failed",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "timestamp": "2026-01-22T10:00:00",
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "message": "Invalid request parameters",
                                      "validationErrors": {
                                        "email": "Invalid email format",
                                        "password": "Password must be at least 8 characters"
                                      }
                                    }
                                    """
                    )
            )
    )

    @ApiResponse(
            responseCode = "409",
            description = "Email already registered",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "timestamp": "2026-01-22T10:00:00",
                                      "status": 409,
                                      "error": "Conflict",
                                      "message": "Email already registered: user@example.com"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Parameter(
                    description = "User registration details including email, password, first name, last name, and role",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "firstname": "John",
                                              "lastname": "Doe",
                                              "email": "john.doe@example.com",
                                              "password": "SecurePass123!",
                                              "role": "USER"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with email and password. Returns JWT access and refresh tokens. Also updates last login timestamp and IP address."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful - Returns JWT tokens",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials - Wrong email or password",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2026-01-22T10:00:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid email or password"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Account disabled or locked"
            )
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Parameter(
                    description = "User login credentials",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "john.doe@example.com",
                                              "password": "SecurePass123!"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletRequest httpServletRequest,
            @Parameter(description = "Client IP address (forwarded from proxy)", example = "192.168.1.100")
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress
    ) {
        // Provide default if header missing
        String clientIp = ipAddress != null ? ipAddress : getClientIpFromRequest(httpServletRequest);

        authService.updateLastLogin(request.email(), clientIp);
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(
            summary = "Get current user",
            description = "Retrieves the currently authenticated user's profile information from JWT token",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrentUserDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "email": "john.doe@example.com",
                                              "firstname": "John",
                                              "lastname": "Doe",
                                              "role": "USER",
                                              "lastLogin": "2026-01-22T10:00:00",
                                              "ipAddress": "192.168.1.100"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated - No valid JWT token provided"
            )
    })
    @GetMapping("/whoami")
    public ResponseEntity<CurrentUserDto> whoami(HttpServletRequest request) {
        return ResponseEntity.ok(authService.whoami(request));
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token. Send refresh token in Authorization header as 'Bearer {token}'",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully - Returns new access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            )
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        AuthenticationResponse response = authService.refreshToken(authHeader);
        return ResponseEntity.ok(response);
    }

    private String getClientIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}