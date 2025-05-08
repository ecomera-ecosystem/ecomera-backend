package com.moushtario.ecomera.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moushtario.ecomera.Configuration.JwtService;
import com.moushtario.ecomera.token.*;
import com.moushtario.ecomera.user.Role;
import com.moushtario.ecomera.user.User;
import com.moushtario.ecomera.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user, save it to DB and return a generated token.
     * @param request custom request object containing user data (credentials + firstname + lastname)
     * @return custom authentication response containing a jwt token
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        var savedUser = userRepository.save(user); // Save the user to the database

        // Generate a JWT token for the newly registered user
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        // Save the token to the database
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticate a user and return a generated token.
     * @param request custom request object containing user email and password
     * @return custom authentication response containing a jwt token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // Throw any exception not important

        var jwtToken = jwtService.generateToken(user); // Generate a JWT token for the authenticated user
        var refreshToken = jwtService.generateRefreshToken(user); // Generate a refresh token for the authenticated user

        // Revoke (cancel) all previous tokens for the user
        revokeAllUserTokens(user);
        // Save the token to the database
        saveUserToken(user, jwtToken);

        // Return the generated token in the response
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Save a user token to the database.
     * @param user the user to whom the token belongs
     * @param jwtToken the generated JWT token
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    /**
     * Revoke all tokens for a user.
     * @param user the user whose tokens will be revoked
     */
    private void revokeAllUserTokens(User user) {
        // Find a list of all valid tokens for the user
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        // If the list is empty, do nothing
        if (validUserTokens.isEmpty())
            return;
        // Otherwise, set all tokens to expired and revoked
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        // Save changes to the database
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refresh the token for a user.
     * @param request the HTTP request containing the authorization header with the refresh token
     * @param response the HTTP response to send the new token
     * @throws IOException if an I/O error occurs while writing the response
     */
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        // Check if the authorization header is present and starts with "Bearer "
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        // Extract the refresh token from the authorization header
        refreshToken = authHeader.substring(7);
        // Extract the user email from the refresh token
        userEmail = jwtService.extractUsername(refreshToken);
        // Check if the user email is not null and retrieve the user from the database
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            // Check if the refresh token is valid for the user
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user); // Generate a new access token for the user
                revokeAllUserTokens(user); // Revoke all previous tokens for the user
                saveUserToken(user, accessToken); // Save the new access token to the database

                // Create a new authentication response with the new access token and refresh token
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                // Set the content type to JSON
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
