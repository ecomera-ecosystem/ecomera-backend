package com.moushtario.ecomera.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

// This class is used to configure OpenAPI documentation (Swagger) for the application.
@OpenAPIDefinition(
        // This is the information about the API. It includes the title, version, description, and contact information.
        info = @Info(
                contact = @Contact(
                        name = "Youssef",
                        email = "youssef.ammari.795@gmail.com",
                        url = "https://youssef-ammari.vercel.app"
                ),
                description = "OpenApi documentation for Ecomera the e-commerce application",
                title = "OpenApi specification - Youssef Ammari (Moushtario)",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://ecomera.onrender.com"
                )
        },
        security = {
                // This is the security requirement for the API. It indicates that the API requires a bearer token for authentication.
                @SecurityRequirement(
                        name = "bearerAuth" // This is the name of the security scheme defined below
                )
        }
)
// A security scheme is a way to define how the API is secured.
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
