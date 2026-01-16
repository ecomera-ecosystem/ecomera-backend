package com.youssef.ecomera;

import com.youssef.ecomera.auth.service.AuthenticationService;
import com.youssef.ecomera.auth.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static com.youssef.ecomera.user.entity.enums.Role.*;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class EcomeraApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomeraApplication.class, args);
    }

    /**
     * Command line runner to create an admin and a manager user on startup
     *
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
            log.info("Admin access token: {}", authService.register(admin).getAccessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .email("manager@mail.com")
                    .password("password")
                    .role(MANAGER)
                    .build();
            log.info("Manager access token: {}", authService.register(manager).getAccessToken());

            var ysf = RegisterRequest.builder()
                    .firstname("Youssef")
                    .lastname("Ammari")
                    .email("youssef.ammari.795@gmail.com")
                    .password("12345678")
                    .role(USER)
                    .build();
            log.info("YSF access token: {} ", authService.register(ysf).getAccessToken());

        };
    }

}
