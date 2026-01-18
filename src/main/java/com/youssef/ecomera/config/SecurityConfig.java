package com.youssef.ecomera.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.youssef.ecomera.user.entity.enums.Permission.*;
import static com.youssef.ecomera.user.entity.enums.Role.*;
import static org.springframework.http.HttpMethod.*;

/**
 * Spring Security configuration for Ecomera.
 * Configures JWT authentication, role-based and authority-based access, and Swagger whitelist.
 *
 * @author Youssef
 * @version 2.0
 * @created 16/01/2026
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
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
            "/swagger-ui.html",
            "/context-path/v3/api-docs"
    };

    private static final String ADMIN_ENDPOINT = "/api/admin/**";
    private static final String MANAGEMENT_ENDPOINT = "/api/management/**";


    private final Filter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            return http
                    .csrf(csrf -> csrf.ignoringRequestMatchers(WHITE_LIST_URL))
                    .authorizeHttpRequests(req -> req
                            // Swagger + Auth endpoints open
                            .requestMatchers(WHITE_LIST_URL).permitAll()

                            // Role-based access for management endpoints
                            .requestMatchers(MANAGEMENT_ENDPOINT).hasAnyRole(ADMIN.name(), MANAGER.name())

                            // Authority-based access for management endpoints (fine-grained)
                            .requestMatchers(GET, MANAGEMENT_ENDPOINT).hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                            .requestMatchers(POST, MANAGEMENT_ENDPOINT).hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                            .requestMatchers(PUT, MANAGEMENT_ENDPOINT).hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                            .requestMatchers(DELETE, MANAGEMENT_ENDPOINT).hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())

                            // Role-based access for admin endpoints
                            .requestMatchers(ADMIN_ENDPOINT).hasRole(ADMIN.name())

                            // Authority-based access for admin endpoints
                            .requestMatchers(GET, ADMIN_ENDPOINT).hasAuthority(ADMIN_READ.name())
                            .requestMatchers(POST, ADMIN_ENDPOINT).hasAuthority(ADMIN_CREATE.name())
                            .requestMatchers(PUT, ADMIN_ENDPOINT).hasAuthority(ADMIN_UPDATE.name())
                            .requestMatchers(DELETE, ADMIN_ENDPOINT).hasAuthority(ADMIN_DELETE.name())

                            // All other requests require authentication
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .logout(logout -> logout
                            .logoutUrl("/api/auth/logout")
                            .addLogoutHandler(logoutHandler)
                            .logoutSuccessHandler((req, res, auth) -> SecurityContextHolder.clearContext())
                    )
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Security configuration failed", e); // TODO: work on a proper exception handling here in issue #5
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200", "https://ecomera.vercel.app"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
