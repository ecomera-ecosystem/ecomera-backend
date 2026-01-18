package com.youssef.ecomera.auth.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.youssef.ecomera.auth.dto.AuthenticationRequest;
import com.youssef.ecomera.auth.dto.AuthenticationResponse;
import com.youssef.ecomera.auth.dto.CurrentUserDto;
import com.youssef.ecomera.auth.service.AuthenticationService;
import com.youssef.ecomera.auth.dto.RegisterRequest;
import com.youssef.ecomera.user.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {

        // Provide default if header missing
        String clientIp = ipAddress != null ? ipAddress : getClientIpFromRequest(httpServletRequest);

        authService.updateLastLogin(request.getEmail(), clientIp);
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDto> me(HttpServletRequest request) {
        var principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getName(); // Comes from JWT
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var dto = CurrentUserDto.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .role(user.getRole().name())
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin().toString()
                        : LocalDateTime.now().toString())
                .ipAddress(user.getIpAddress())
                .build();

        return ResponseEntity.ok(dto);
    }

    private String getClientIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
