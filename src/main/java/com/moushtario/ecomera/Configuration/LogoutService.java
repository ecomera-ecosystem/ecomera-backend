package com.moushtario.ecomera.Configuration;

import com.moushtario.ecomera.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // If no token is present, exit the method
        }

        jwt = authHeader.substring(7); // Extract the json web token by removing "Bearer " prefix

        // Check if the token is present in the database and bring it in
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        // If the token is present, set it as expired and revoked
        if(storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            // clear the authentication context
            SecurityContextHolder.clearContext();
        }
    }
}
