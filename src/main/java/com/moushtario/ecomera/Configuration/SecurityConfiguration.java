package com.moushtario.ecomera.Configuration;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

//import static com.moushtario.ecomera.user.Permission.*;
import static com.moushtario.ecomera.user.Role.*;
//import static org.springframework.http.HttpMethod.*;

/**
 * @author Youssef
 * @version 2.0
 * @created 09/05/2025
 */

@Configuration
@EnableWebSecurity // Enable Spring Security @configuration & @EnableWebSecurity are mandatory in spring boot 3.0
@EnableMethodSecurity // Enable method security which allows us to use @PreAuthorize and @PostAuthorize annotations ...etc
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {
            "/api/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private final Filter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        try {
            return http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                    .authorizeHttpRequests(req -> req
                            .requestMatchers(WHITE_LIST_URL).permitAll() // White Listing: Allow access to authentication endpoints (login, register)
                            .requestMatchers("/api/management/**").hasAnyRole(ADMIN.name(), MANAGER.name()) // Allow access to management endpoints for ADMIN and MANAGER roles
                            // Use either Roles or Authorities you can uncomment next lines but you'll have to comment/remove hasAnyRole(ADMIN.name(), MANAGER.name())

//                            .requestMatchers(GET, "/api/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
//                            .requestMatchers(POST, "/api/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
//                            .requestMatchers(PUT, "/api/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
//                            .requestMatchers(DELETE, "/api/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
//

                            /* .requestMatchers("/api/admin/**").hasRole(ADMIN.name())

                             .requestMatchers(GET, "/api/admin/**").hasAuthority(ADMIN_READ.name())
                             .requestMatchers(POST, "/api/admin/**").hasAuthority(ADMIN_CREATE.name())
                             .requestMatchers(PUT, "/api/admin/**").hasAuthority(ADMIN_UPDATE.name())
                             .requestMatchers(DELETE, "/api/admin/**").hasAuthority(ADMIN_DELETE.name())*/
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
