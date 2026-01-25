package com.youssef.ecomera.domain.order.dto.orderItem;

import com.youssef.ecomera.domain.order.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


/**
 * Read DTO for {@link OrderItem}
 */
@Builder
@Schema(name = "OrderItemDto", description = "Represents an order item with pricing and metadata")
public record OrderItemDto(

        @Schema(description = "Unique identifier of the order item", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unit price of the product", example = "49.99")
        BigDecimal unitPrice,

        @Schema(description = "Quantity of the product in the order", example = "2")
        Integer quantity,

        @Schema(description = "UUID of the order this item belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID orderId,

        @Schema(description = "UUID of the product being ordered", example = "987e6543-e21b-12d3-a456-426614174999")
        UUID productId,

        @Schema(description = "Timestamp when the order item was created", example = "2026-01-25T19:12:54")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the order item was last updated", example = "2026-01-26T10:45:00")
        LocalDateTime updatedAt
) {
}