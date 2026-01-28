package com.youssef.ecomera.domain.order.dto.orderitem;

import com.youssef.ecomera.domain.order.entity.OrderItem;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Read DTO for {@link OrderItem}
 */
@Builder
@Schema(name = "OrderItemCreateDto", description = "Payload for creating a new order item")
public record OrderItemCreateDto(

        @Schema(description = "Quantity of the product in the order", example = "2")
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @Schema(description = "UUID of the product being ordered", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "Product ID is required")
        UUID productId,

        @Schema(description = "UUID of the order this item belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "Order ID is required")
        UUID orderId
) {}

