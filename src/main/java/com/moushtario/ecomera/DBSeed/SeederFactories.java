package com.moushtario.ecomera.DBSeed;

import com.github.javafaker.Faker;
import com.moushtario.ecomera.mvc.domain.entity.Category;
import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.domain.enums.CategoryType;
import com.moushtario.ecomera.mvc.domain.enums.OrderStatus;
import com.moushtario.ecomera.user.Role;
import com.moushtario.ecomera.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SeederFactories {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    private SeederFactories() {
        // Private constructor to prevent instantiation
    }

    public static User seederUser() {
        Role[] allRoles = Role.values();
        List<Role> roleList = new ArrayList<>(Arrays.asList(allRoles));

        return User.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .role(roleList.get(random.nextInt(roleList.size())))
                .build();
    }

    public static Product seederProduct() {
        return Product.builder()
                .title(faker.commerce().productName())
                .stock(random.nextInt(100))
                .description(faker.lorem().word())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 3, 1000)))
                .imageUrl(faker.internet().image())
                .category(CategoryType.values()[random.nextInt(CategoryType.values().length)])
    //                    .category(Category.builder()
    //                            .name(CategoryType.values()[random.nextInt(CategoryType.values().length)])
    //                            .description(faker.lorem().word())
    //                            .build())
                .build();
    }

    public static List<Category> seederCategory() {
        CategoryType[] allTypes = CategoryType.values();
        List<CategoryType> remainingTypes = new ArrayList<>(Arrays.asList(allTypes));

        List<Category> category_records = new ArrayList<>();

        for (CategoryType type : remainingTypes) {
            Category category = Category.builder()
                    .name(type)
                    .description(faker.lorem().sentence())
                    .imageUrl(faker.internet().image())
                    .build();
            category_records.add(category);
        }
        return category_records;
    }

    public static Order seederOrder(User user) {

        return Order.builder()
                .status(OrderStatus.values()[random.nextInt(OrderStatus.values().length)])
                .orderDate(LocalDateTime.now())
                .totalPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 3, 1000)))
                .user(user)
                .build();
    }
}
