package com.moushtario.ecomera.auth;

import com.moushtario.ecomera.Configuration.JwtService;
import com.moushtario.ecomera.user.Role;
import com.moushtario.ecomera.user.User;
import com.moushtario.ecomera.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Youssef
 * @version 1.0
 * @created 16/04/2025
 */
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
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

        userRepository.save(user); // Save the user to the database
        // Generate a JWT token for the newly registered user
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
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
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
