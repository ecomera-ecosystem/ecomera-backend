package com.moushtario.ecomera.Configuration;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @author Youssef
 * @version 1.0
 * @created 16/04/2025
 */

@Configuration
@EnableWebSecurity // Enable Spring Security @configuration & @EnableWebSecurity are mandatory in spring boot 3.0
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final Filter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        try {
            return http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                    .authorizeHttpRequests(req -> req
                    .requestMatchers("/api/auth/**").permitAll() // White Listing: Allow access to authentication endpoints (login, register)
                    .anyRequest().authenticated() // Require authentication for all other requests
                            )
                    .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
                    .authenticationProvider(authenticationProvider) // Set the authentication provider
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before the UsernamePasswordAuthenticationFilter
//                    .httpBasic(AbstractHttpConfigurer::disable);// Disable basic authentication
                    .logout(logout -> logout
                            .logoutUrl("/api/auth/logout") // Set the logout URL
                            .addLogoutHandler(logoutHandler) // Handle the logout
                            .logoutSuccessHandler((req, res, auth) -> SecurityContextHolder.clearContext()) // Clear security context on logout
                    )
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
