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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before the UsernamePasswordAuthenticationFilter
//                    .httpBasic(AbstractHttpConfigurer::disable);// Disable basic authentication
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
