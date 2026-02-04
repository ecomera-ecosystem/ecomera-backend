package com.youssef.ecomera.auth.service;

import com.youssef.ecomera.auth.dto.AuthenticationRequest;
import com.youssef.ecomera.auth.dto.AuthenticationResponse;
import com.youssef.ecomera.auth.dto.CurrentUserDto;
import com.youssef.ecomera.auth.dto.RegisterRequest;
import com.youssef.ecomera.auth.entity.Token;
import com.youssef.ecomera.auth.enums.TokenType;
import com.youssef.ecomera.auth.repository.TokenRepository;
import com.youssef.ecomera.auth.security.JwtService;
import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.common.exception.UnauthorizedException;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final String FIELD_EMAIL = "email";

    /**
     * Register a new user, save it to DB and return a generated token.
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered: " + request.email());
        }

        // Build user with SuperBuilder (because User extends BaseEntity)
        User user = User.builder()
                .firstName(request.firstname())
                .lastName(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        // Generate tokens
        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Save access token
        saveUserToken(savedUser, jwtToken, TokenType.BEARER);
        saveUserToken(savedUser, refreshToken, TokenType.REFRESH);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticate a user and return a generated token.
     */
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Find user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", FIELD_EMAIL, request.email()));

        // Generate tokens
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Revoke old tokens and save new one
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken, TokenType.BEARER);
        saveUserToken(user, refreshToken, TokenType.REFRESH);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User authenticated successfully: {}", user.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Save a user token to the database.
     */
    private void saveUserToken(User user, String jwtToken, TokenType tokenType) {
        Token token = Token.builder()
                .user(user)
                .value(jwtToken)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revoke all tokens for a user.
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refresh the token for a user.
     */
    @Transactional
    public AuthenticationResponse refreshToken(String authHeader) {
        String refreshToken = authHeader.substring(7);

        // Validate refresh token
        Token storedToken = tokenRepository.findByValue(refreshToken)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (storedToken.isRevoked() || storedToken.isExpired()) {
            throw new BusinessException("Refresh token invalid");
        }

        User user = storedToken.getUser();

        String accessToken = jwtService.generateToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, accessToken, TokenType.BEARER);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Update the last login time of a user.
     */
    @Transactional
    public void updateLastLogin(String email, String ip) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", FIELD_EMAIL, email));

        user.setLastLogin(LocalDateTime.now());
        user.setIpAddress(ip);
    }

    public CurrentUserDto whoami(HttpServletRequest request) {
        var principal = request.getUserPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("No authenticated principal found");
        }

        String email = principal.getName(); // Comes from JWT
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", FIELD_EMAIL, email));

        return CurrentUserDto.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .role(user.getRole().name())
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin().toString()
                        : LocalDateTime.now().toString())
                .ipAddress(user.getIpAddress())
                .build();
    }
}