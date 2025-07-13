package com.moushtario.ecomera.DBSeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seed")
@Profile("dev")
public class SeederController {

    @Autowired
    private Seeder databaseSeeder;

    @Value("${app.seed-database:false}") // Read from properties file
    private boolean seedDatabase;

    @PostMapping
    public String seedDatabase() {
        if (!seedDatabase) {
            return "Database seeding is disabled. Skipping..."; // Exit method if seeding is disabled
        }
//        seedDatabase = true; // Set to true to allow seeding
        databaseSeeder.run(); // Manually trigger the seeder
        return "Database seeding completed!";
    }
}