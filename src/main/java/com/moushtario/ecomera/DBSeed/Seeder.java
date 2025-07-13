package com.moushtario.ecomera.DBSeed;

import com.github.javafaker.Faker;
import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.repository.CategoryRepository;
import com.moushtario.ecomera.mvc.repository.OrderItemRepository;
import com.moushtario.ecomera.mvc.repository.OrderRepository;
import com.moushtario.ecomera.mvc.repository.ProductRepository;
import com.moushtario.ecomera.user.User;
import com.moushtario.ecomera.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();


    @Override
    public void run(String... args) {

        // Fake Users
        for (int i = 0; i < 3; i++) {
            User user = SeederFactories.seederUser();
            // Save user to the database
             userRepository.save(user);
        }
        // Generate Fake Orders
        for (int i = 0; i < 2; i++) {
            List<User> users = userRepository.findAll();
            User user = users.get(random.nextInt(users.size()));

            Order order = SeederFactories.seederOrder(user);
            orderRepository.save(order);
        }

        // Generate Fake Categories
        // Create one category per type
        categoryRepository.saveAll(SeederFactories.seederCategory());

        // Generate Fake Products
        for (int i = 0; i < 8; i++) {
            Product product = SeederFactories.seederProduct();
            productRepository.save(product);
        }

//        List<Product> products = productRepository.findAll();

//        System.out.println("Database seeding completed!");
        log.info("Database seeding completed!");
//        System.out.println("Products in the database:");
//        products.forEach(System.out::println);
    }
}