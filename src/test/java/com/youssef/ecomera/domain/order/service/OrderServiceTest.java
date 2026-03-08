package com.youssef.ecomera.domain.order.service;

import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.cart.entity.Cart;
import com.youssef.ecomera.domain.cart.repository.CartRepository;
import com.youssef.ecomera.domain.order.dto.order.OrderCreateDto;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.domain.order.mapper.OrderMapper;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.repository.ProductRepository;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.repository.UserRepository;
import com.youssef.ecomera.utils.TestSuiteUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    CartRepository cartRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    OrderMapper orderMapper;

    @InjectMocks
    OrderService orderService;

    private User user;
    private Product product;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        user = TestSuiteUtils.createUser();
        product = TestSuiteUtils.createProduct();
        order = TestSuiteUtils.createOrder();
        orderDto = TestSuiteUtils.createOrderDto();
    }

    @AfterEach
    void tearDown() {
        user = null;
        product = null;
        order = null;
        orderDto = null;
    }

    // ─── create ──────────────────────────────────────────────────────────────────

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderItemCreateDto itemDto = new OrderItemCreateDto(2, product.getId());
        OrderCreateDto createDto = new OrderCreateDto(user.getId(), List.of(itemDto));

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(orderMapper.toEntity(createDto)).willReturn(order);
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(orderRepository.save(order)).willReturn(order);
        given(orderMapper.toDto(order)).willReturn(orderDto);

        OrderDto result = orderService.create(createDto);

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void shouldThrowWhenOrderCreatedWithEmptyItems() {
        OrderCreateDto createDto = new OrderCreateDto(user.getId(), List.of());

        assertThatThrownBy(() -> orderService.create(createDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        OrderItemCreateDto itemDto = new OrderItemCreateDto(2, product.getId());
        OrderCreateDto createDto = new OrderCreateDto(user.getId(), List.of(itemDto));

        given(userRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(createDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenInsufficientStockOnCreate() {
        product.setStock(1);
        OrderItemCreateDto itemDto = new OrderItemCreateDto(5, product.getId());
        OrderCreateDto createDto = new OrderCreateDto(user.getId(), List.of(itemDto));

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(orderMapper.toEntity(createDto)).willReturn(order);
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.create(createDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("stock");
    }

    // ─── getById ─────────────────────────────────────────────────────────────────

    @Test
    void shouldGetOrderByIdSuccessfully() {
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(orderMapper.toDto(order)).willReturn(orderDto);

        OrderDto result = orderService.getById(order.getId());

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    void shouldThrowWhenOrderNotFoundById() {
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(order.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── updateStatus ─────────────────────────────────────────────────────────────

    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        OrderUpdateDto updateDto = new OrderUpdateDto(OrderStatus.PROCESSING);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(orderRepository.save(order)).willReturn(order);
        given(orderMapper.toDto(order)).willReturn(orderDto);

        OrderDto result = orderService.updateStatus(order.getId(), updateDto);

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(order);
    }

    // ─── deleteById ───────────────────────────────────────────────────────────────

    @Test
    void shouldDeleteOrderSuccessfully() {
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        orderService.deleteById(order.getId());

        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void shouldThrowWhenOrderNotFoundOnDelete() {
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteById(order.getId()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).delete(any(Order.class));
    }

    // ─── addItemToOrder ───────────────────────────────────────────────────────────

    @Test
    void shouldAddItemToOrderSuccessfully() {
        order.setStatus(OrderStatus.PENDING);
        OrderItemCreateDto itemDto = new OrderItemCreateDto(2, product.getId());

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(orderRepository.save(order)).willReturn(order);
        given(orderMapper.toDto(order)).willReturn(orderDto);

        OrderDto result = orderService.addItemToOrder(order.getId(), itemDto);

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void shouldThrowWhenOrderIsShippedOnAddItem() {
        order.setStatus(OrderStatus.SHIPPED);
        OrderItemCreateDto itemDto = new OrderItemCreateDto(2, product.getId());

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.addItemToOrder(order.getId(), itemDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot modify order");
    }

    // ─── removeItemFromOrder ──────────────────────────────────────────────────────

    @Test
    void shouldRemoveItemFromOrderSuccessfully() {
        order.setStatus(OrderStatus.PENDING);
        OrderItem item = TestSuiteUtils.createOrderItem(product, order);
        OrderItem item2 = TestSuiteUtils.createOrderItem(product, order);
        order.getOrderItems().add(item);
        order.getOrderItems().add(item2);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(orderRepository.save(order)).willReturn(order);
        given(orderMapper.toDto(order)).willReturn(orderDto);

        orderService.removeItemFromOrder(order.getId(), item.getId());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void shouldThrowWhenRemovingLastItemFromOrder() {
        // Create fresh order with exactly one item
        Order freshOrder = Order.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .user(user)
                .build();

        OrderItem item = TestSuiteUtils.createOrderItem(product, freshOrder);
        freshOrder.getOrderItems().add(item);

        assertThat(freshOrder.getOrderItems()).hasSize(1);

        given(orderRepository.findById(freshOrder.getId())).willReturn(Optional.of(freshOrder));

        assertThatThrownBy(() -> orderService.removeItemFromOrder(freshOrder.getId(), item.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot remove last item");
    }

    // ─── checkout ────────────────────────────────────────────────────────────────

    @Test
    void shouldCheckoutSuccessfully() {
        Cart cart = TestSuiteUtils.createCartWithItems(user, product);

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(orderRepository.save(any(Order.class))).willReturn(order);
        given(orderMapper.toDto(order)).willReturn(orderDto);

        OrderDto result = orderService.checkout(user.getId());

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void shouldThrowWhenCheckoutWithEmptyCart() {
        Cart emptyCart = TestSuiteUtils.createCart(user);

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(emptyCart));

        assertThatThrownBy(() -> orderService.checkout(user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empty cart");
    }

    @Test
    void shouldThrowWhenInsufficientStockOnCheckout() {
        product.setStock(0);
        Cart cart = TestSuiteUtils.createCartWithItems(user, product);

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        assertThatThrownBy(() -> orderService.checkout(user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("stock");
    }
}