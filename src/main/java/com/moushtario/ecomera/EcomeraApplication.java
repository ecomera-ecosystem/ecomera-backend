package com.moushtario.ecomera;

import com.moushtario.ecomera.auth.service.AuthenticationService;
import com.moushtario.ecomera.auth.dto.RegisterRequest;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.moushtario.ecomera.user.Role.ADMIN;
import static com.moushtario.ecomera.user.Role.MANAGER;

@SpringBootApplication
public class EcomeraApplication {

	public static void main(String[] args) {

		// Load .env file
		Dotenv dotenv = Dotenv.load();

		// Set system properties
		// Loop over all env variables and set them as system properties
		dotenv.entries().forEach(entry -> {
			if (entry.getValue() != null) {
				System.setProperty(entry.getKey(), entry.getValue());
			} else {
				throw new RuntimeException("❌ Missing value for key: " + entry.getKey());
			}
		});

		// Run the application
		SpringApplication.run(EcomeraApplication.class, args);
	}

	/**
	 * Command line runner to create an admin and a manager user on startup
	 * @param authService the authentication service for registering & authenticating users
	 * @return access tokens for an admin and a manager
	 */
	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService authService
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("admin@mail.com")
					.password("password")
					.role(ADMIN)
					.build();
			System.out.println("Admin access token: " + authService.register(admin).getAccessToken());

			var manager = RegisterRequest.builder()
					.firstname("Manager")
					.lastname("Manager")
					.email("manager@mail.com")
					.password("password")
					.role(MANAGER)
					.build();
			System.out.println("Manager access token: " + authService.register(manager).getAccessToken());

		};
	}

}
