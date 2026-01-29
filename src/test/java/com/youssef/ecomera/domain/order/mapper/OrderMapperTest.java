package com.youssef.ecomera.domain.order.mapper;

import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemDto;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.utils.TestLogger;
import com.youssef.ecomera.utils.TestSuiteUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderMapperTest {

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderMapperImpl mapper;

    // Counter
    private static int testCounter = 1;
    private static Instant suiteStartTime;

    @BeforeAll
    static void beginTestExecution() {
        suiteStartTime = Instant.now();
        TestLogger.logSuiteStart(OrderMapperTest.class);
    }

    @AfterAll
    static void endTestExecution() {
        TestLogger.logSuiteEnd(OrderMapperTest.class, suiteStartTime);
    }

    @BeforeEach
    void setUp() {
        TestLogger.logTestStart(testCounter);

        when(orderItemMapper.toDtoList(anyList())).thenAnswer(invocation -> {
            List<OrderItem> items = invocation.getArgument(0);
            if (items == null || items.isEmpty()) {
                return new ArrayList<>();
            }

            // Map each OrderItem to OrderItemDto
            return items.stream()
                    .map(item -> new OrderItemDto(
                            item.getId(),
                            item.getUnitPrice(),
                            item.getQuantity(),
                            item.getOrder().getId(),
                            item.getProduct().getId(),
                            item.getCreatedAt(),
                            item.getUpdatedAt()
                    ))
                    .toList();
        });
    }

    @AfterEach
    void tearDown() {
        TestLogger.logTestEnd(testCounter);
        mapper = null;
        testCounter++;
    }

    @Nested
    @DisplayName("Convert Entity to DTO")
    class toDto {

        @Test
        @DisplayName("Should map complete Order to OrderDto with all relationships")
        void should_map_complete_order_to_dto() {
            // Given
            Order order = TestSuiteUtils.createOrder();

            // When
            OrderDto result = mapper.toDto(order);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(order.getId());
            assertThat(result.status()).isEqualTo(order.getStatus());
            assertThat(result.totalPrice()).isEqualByComparingTo(order.getTotalPrice());
            assertThat(result.userId()).isEqualTo(order.getUser().getId());
            assertThat(result.orderItems()).hasSameSizeAs(order.getOrderItems());

            verify(orderItemMapper).toDtoList(order.getOrderItems());
        }

        @Test
        @DisplayName("Should map Order items with correct product references")
        void should_map_order_items_correctly() {
            // Given
            Order order = TestSuiteUtils.createOrderWithItems(3);

            // When
            OrderDto result = mapper.toDto(order);

            // Then
            assertThat(result.orderItems()).hasSize(3);

            for (int i = 0; i < order.getOrderItems().size(); i++) {
                OrderItem originalItem = order.getOrderItems().get(i);
                OrderItemDto mappedItem = result.orderItems().get(i);

                assertThat(mappedItem.id()).isEqualTo(originalItem.getId());
                assertThat(mappedItem.quantity()).isEqualTo(originalItem.getQuantity());
                assertThat(mappedItem.unitPrice()).isEqualByComparingTo(originalItem.getUnitPrice());
                assertThat(mappedItem.productId()).isEqualTo(originalItem.getProduct().getId());
            }
        }

        @Test
        @DisplayName("Should handle Order with empty items list")
        void should_handle_empty_order_items() {
            // Given
            Order order = TestSuiteUtils.createMinimalOrder();

            // When
            OrderDto result = mapper.toDto(order);

            // Then
            assertThat(result.orderItems()).isEmpty();
            assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should return null when Order is null")
        void should_return_null_for_null_order() {
            // When
            OrderDto result = mapper.toDto(null);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should preserve BigDecimal precision")
        void should_preserve_BigDecimal_precision() {
            // Given
            Order order = TestSuiteUtils.createMinimalOrder();
            order.setTotalPrice(new BigDecimal("123.456789"));

            // When
            OrderDto result = mapper.toDto(order);

            // Then
            assertThat(result.totalPrice())
                    .isEqualByComparingTo(new BigDecimal("123.456789"));
        }
    }

    @Nested
    @DisplayName("Convert DTO to Entity")
    class ToEntity {

        @Test
        @DisplayName("Should map OrderDto to Order entity")
        void should_map_dto_to_entity() {
            // Given
            OrderDto dto = TestSuiteUtils.createOrderDto();

            // When
            Order result = mapper.toEntity(dto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(dto.status());
            assertThat(result.getTotalPrice()).isEqualByComparingTo(dto.totalPrice());
        }

        @Test
        @DisplayName("Should return null when DTO is null")
        void should_return_null_for_null_dto() {
            // When
            Order result = mapper.toEntity((OrderDto) null);

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Update Entity from DTO")
    class UpdateEntityFromDto {

        @Test
        @DisplayName("Should update existing Order from DTO")
        void should_update_order_from_dto() {
            // Given
            Order existingOrder = TestSuiteUtils.createOrder();

            OrderUpdateDto updateDto = OrderUpdateDto.builder()
                    .status(OrderStatus.SHIPPED)
                    .build();

            // When
            mapper.updateEntityFromDto(updateDto, existingOrder);

            // Then
            assertThat(existingOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        @DisplayName("Should handle null DTO gracefully")
        void should_handle_null_dto() {
            // Given
            Order existingOrder = TestSuiteUtils.createOrder();
            OrderStatus originalStatus = existingOrder.getStatus();

            // When
            mapper.updateEntityFromDto(null, existingOrder);

            // Then
            assertThat(existingOrder.getStatus()).isEqualTo(originalStatus); // Should remain unchanged
        }
    }

    @Nested
    @DisplayName("Map Status")
    class MapStatus {

        @Test
        @DisplayName("Should map all OrderStatus values correctly")
        void should_map_all_status_values() {
            // Test each status
            for (OrderStatus status : OrderStatus.values()) {
                // Given
                Order order = TestSuiteUtils.createMinimalOrder();
                order.setStatus(status);

                // When
                OrderDto result = mapper.toDto(order);

                // Then
                assertThat(result.status()).isEqualTo(status);
            }
        }

        @Test
        @DisplayName("Should handle status transition from PENDING to DELIVERED")
        void should_handle_status_transition() {
            // Given
            Order order = TestSuiteUtils.createMinimalOrder();
            order.setStatus(OrderStatus.PENDING);

            OrderUpdateDto updateDto = OrderUpdateDto.builder()
                    .status(OrderStatus.DELIVERED)
                    .build();

            // When
            mapper.updateEntityFromDto(updateDto, order);

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }
}