package com.youssef.ecomera.utils;

import com.youssef.ecomera.auth.dto.AuthenticationRequest;
import com.youssef.ecomera.auth.dto.RegisterRequest;
import com.youssef.ecomera.auth.entity.Token;
import com.youssef.ecomera.auth.enums.TokenType;
import com.youssef.ecomera.domain.cart.dto.cart.CartCreateDto;
import com.youssef.ecomera.domain.cart.dto.cart.CartDto;
import com.youssef.ecomera.domain.cart.dto.cartitem.CartItemUpdateDto;
import com.youssef.ecomera.domain.cart.entity.Cart;
import com.youssef.ecomera.domain.cart.entity.CartItem;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.enums.PaymentMethod;
import com.youssef.ecomera.domain.payment.enums.PaymentStatus;
import com.youssef.ecomera.domain.product.dto.ProductCreateDto;
import com.youssef.ecomera.domain.product.dto.ProductDto;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.enums.Role;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestSuiteUtils {

    private static final Faker faker = new Faker();

    // Fixed UUIDs for assertions where you need predictability
    public static final UUID TEST_ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID TEST_PRODUCT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID TEST_PRODUCT_ID_2 = UUID.fromString("32333333-3333-3333-3333-333333333333");

    // ================== AUTH ==================
    public RegisterRequest createRegisterRequest() {
        return RegisterRequest.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password("Password123!")
                .role(Role.USER)
                .build();
    }

    public AuthenticationRequest createAuthRequest(String email) {
        return AuthenticationRequest.builder()
                .email(email)
                .password("Password123!")
                .build();
    }

    // ================== USER ==================
    public User createUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email(faker.internet().emailAddress())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .role(Role.USER)
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
        return createProductA();
    }

    public Product createProductA() {
        return Product.builder()
                .id(TEST_PRODUCT_ID)
                .title("Dell XPS 13")
                .description("Powerful and compact laptop with intel i7 processor")
                .imageUrl(faker.internet().image())
                .price(BigDecimal.valueOf(1199.99))
                .stock(50)
                .category(CategoryType.ELECTRONICS)
                .build();
    }

    public Product createProductB() {
        return Product.builder()
                .id(TEST_PRODUCT_ID_2)
                .title("Lenovo ThinkPad X1 Carbon")
                .description("Premium business laptop with intel i7 processor and long battery life")
                .imageUrl(faker.internet().image())
                .price(BigDecimal.valueOf(1199.99))
                .stock(50)
                .category(CategoryType.ELECTRONICS)
                .build();
    }

    public Product createRandomProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
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

    public ProductCreateDto createProductCreateDto() {
        return ProductCreateDto.builder()
                .title(faker.commerce().productName())
                .description(faker.lorem().sentence(10))
                .imageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", "")))
                .stock(faker.number().numberBetween(10, 500))
                .category(faker.options().option(CategoryType.class))
                .build();
    }

    public ProductCreateDto createProductCreateDtoFromProduct(Product product) {
        return ProductCreateDto.builder()
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .build();
    }

    public ProductCreateDto createProductCreateDtoFromProduct() {
        Product product = createProduct();
        return ProductCreateDto.builder()
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .build();
    }

    public ProductDto createProductDtoFromProduct() {
        Product product = createProduct();
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductDto createProductDtoFromProduct(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<Product> createProductList(){
        return List.of(
                createProductA(),
                createProductB()
        );
    }

    public List<ProductDto> createProductDtoListFromProductList(List<Product> products){
        return List.of(
                createProductDtoFromProduct(products.get(0)),
                createProductDtoFromProduct(products.get(1))
        );
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
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.PAYPAL)
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

    public OrderDto createOrderDto() {
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


    // ================== CART ==================
    public static final UUID TEST_CART_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public static final UUID TEST_CART_ITEM_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    public Cart createCart(User user) {
        return Cart.builder()
                .id(TEST_CART_ID)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
    }

    public Cart createCartWithItems(User user, Product product) {
        Cart cart = createCart(user);
        CartItem item = CartItem.builder()
                .id(TEST_CART_ITEM_ID)
                .cart(cart)
                .product(product)
                .quantity(2)
                .unitPrice(product.getPrice())
                .build();
        cart.getCartItems().add(item);
        return cart;
    }

    public CartDto createCartDto(Cart cart) {
        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .cartItems(new ArrayList<>())
                .build();
    }

    public CartCreateDto createCartCreateDto(Product product) {
        return CartCreateDto.builder()
                .productId(product.getId())
                .quantity(2)
                .build();
    }

    public CartItemUpdateDto createCartItemUpdateDto() {
        return CartItemUpdateDto.builder()
                .quantity(3)
                .build();
    }

}
