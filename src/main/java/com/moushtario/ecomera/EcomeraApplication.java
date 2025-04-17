package com.moushtario.ecomera;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
