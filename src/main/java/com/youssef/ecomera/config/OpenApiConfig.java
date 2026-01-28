package com.youssef.ecomera.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;

/**
 * OpenAPI (Swagger) configuration for the Ecomera application.
 * Provides API metadata, server environments, and JWT security scheme.
 *
 * @author Youssef Ammari
 * @version 0.1.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Ecomera API",
                version = "0.1.0",
                description = """
                        RESTful API for Ecomera E-commerce Platform
                        
                        Features:
                        
                        • JWT-based authentication & authorization
                        
                        • Product catalog management
                        
                        • Order processing & tracking
                        
                        • Payment integration
                        
                        • User profile management
                        
                        • Real-time inventory updates
                        
                        For authentication, use the /api/v1/auth/authenticate endpoint to obtain a JWT token.
                        """,
                termsOfService = "https://ecomera.io/terms",
                contact = @Contact(
                        name = "Youssef Ammari",
                        email = "youssef.ammari.795@gmail.com",
                        url = "https://github.com/Ammari-Youssef"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development",
                        url = "http://localhost:8080/api/{version}",
                        variables = {
                                @ServerVariable(
                                        name = "version",
                                        defaultValue = "v1",
                                        allowableValues = {"v1", "v2"})
                        }
                ),
                @Server(
                        description = "Docker Environment",
                        url = "http://localhost:8080/api/{version}",
                        variables = {
                                @ServerVariable(
                                        name = "version",
                                        defaultValue = "v1",
                                        allowableValues = {"v1", "v2"})
                        }
                ),
                @Server(
                        description = "Production (Railway)",
                        url = "https://ecomera-production.railway.app",
                        variables = {}
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = """
                JWT Authentication
                
                To authenticate:
                1. Send POST request to /api/v1/auth/authenticate with email and password
                2. Copy the 'accessToken' from response
                3. Click 'Authorize' button above
                4. Enter: Bearer {your-access-token}
                5. Click 'Authorize'
                
                Token expires in 1 hour. Use /api/v1/auth/refresh-token to get a new token.
                """,
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}