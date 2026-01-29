package com.youssef.ecomera.utils;

import com.youssef.ecomera.auth.entity.Token;
import com.youssef.ecomera.auth.enums.TokenType;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;
import com.youssef.ecomera.user.entity.User;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@UtilityClass
public class TestSuiteUtils {

    private static final Faker faker = new Faker();

    // Fixed UUIDs for assertions where you need predictability
    public static final UUID TEST_ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID TEST_PRODUCT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    // ================== USER ==================
    public User createUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email(faker.internet().emailAddress())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    public User createUser(UUID id) {
        return User.builder()
                .id(id)
                .email(faker.internet().emailAddress())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    // ================== PRODUCT ==================
    public Product createProduct() {
        return Product.builder()
                .id(TEST_PRODUCT_ID)
                .title(faker.commerce().productName())
                .description(faker.lorem().sentence(10))
                .imageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", "")))
                .stock(faker.number().numberBetween(10, 500))
                .category(faker.options().option(CategoryType.class))
                .build();
    }

    public Product createProduct(UUID id) {
        return Product.builder()
                .id(id)
                .title(faker.commerce().productName())
                .description(faker.lorem().sentence(10))
                .imageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", "")))
                .stock(faker.number().numberBetween(10, 500))
                .category(faker.options().option(CategoryType.class))
                .build();
    }

    public Product createProduct(UUID id, BigDecimal price) {
        return Product.builder()
                .id(id)
                .title(faker.commerce().productName())
                .description(faker.lorem().sentence(10))
                .imageUrl(faker.internet().image())
                .price(price)
                .stock(faker.number().numberBetween(10, 500))
                .category(faker.options().option(CategoryType.class))
                .build();
    }

    // ================== ORDER ITEM ==================
    public OrderItem createOrderItem(Product product, Order order) {
        int quantity = faker.number().numberBetween(1, 10);
        return OrderItem.builder()
                .id(UUID.randomUUID())
                .unitPrice(product.getPrice())
                .quantity(quantity)
                .product(product)
                .order(order)
                .build();
    }

    public OrderItem createOrderItemWithoutOrder(Product product) {
        int quantity = faker.number().numberBetween(1, 10);
        return OrderItem.builder()
                .id(UUID.randomUUID())
                .unitPrice(product.getPrice())
                .quantity(quantity)
                .product(product)
                .build();
    }

    // ================== PAYMENT ==================
    public Payment createPayment() {
        return Payment.builder()
                .id(UUID.randomUUID())
                // Add your payment fields with faker
                .build();
    }

    // ================== ORDER ==================
    public Order createOrder() {
        User user = createUser();
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .user(user)
                .payment(createPayment())
                .orderItems(new ArrayList<>())
                .build();

        // Add 1-3 random items
        int itemCount = faker.number().numberBetween(1, 4);
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < itemCount; i++) {
            Product product = createProduct();
            OrderItem item = createOrderItem(product, order);
            order.getOrderItems().add(item);

            BigDecimal itemTotal = item.getUnitPrice()
                    .multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }

        order.setTotalPrice(total);
        return order;
    }

    public Order createOrderWithFixedId() {
        Order order = createOrder();
        order.setId(TEST_ORDER_ID);
        return order;
    }

    public Order createOrderWithItems(int itemCount) {
        User user = createUser();
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .status(faker.options().option(OrderStatus.class))
                .user(user)
                .payment(createPayment())
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < itemCount; i++) {
            Product product = createProduct(UUID.randomUUID());
            OrderItem item = createOrderItem(product, order);
            order.getOrderItems().add(item);

            BigDecimal itemTotal = item.getUnitPrice()
                    .multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }

        order.setTotalPrice(total);
        return order;
    }

    public Order createMinimalOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .user(createUser())
                .orderItems(new ArrayList<>())
                .build();
    }

    public OrderDto createOrderDto(){
        return OrderDto.builder()
                .id(TEST_ORDER_ID)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .userId(createUser().getId())
                .orderItems(new ArrayList<>())
                .build();
    }

    // ================== TOKEN ==================

    public Token createBearerToken(User user, String tokenValue) {
        return Token.builder()
                .user(user)
                .value(tokenValue)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    public Token createRefreshToken(User user, String tokenValue) {
        return Token.builder()
                .user(user)
                .value(tokenValue)
                .tokenType(TokenType.REFRESH)
                .expired(false)
                .revoked(false)
                .build();
    }


}
