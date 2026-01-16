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

/**
 * OpenAPI (Swagger) configuration for the Ecomera application.
 * Provides API metadata, server environments, and JWT security scheme.
 *
 * @author Youssef Ammari
 * @version 1.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Ecomera API Specification",
                version = "1.0",
                description = "Interactive OpenAPI documentation for the Ecomera e-commerce backend.",
                termsOfService = "https://ecomera-production.railway.app/terms",
                contact = @Contact(
                        name = "Youssef Ammari",
                        email = "youssef.ammari.795@gmail.com",
                        url = "https://youssef-ammari.vercel.app"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production Environment",
                        url = "https://ecomera-production.railway.app"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT-based authentication. Provide the token in the Authorization header using the Bearer scheme.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
