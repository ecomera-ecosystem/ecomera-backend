package com.youssef.ecomera.domain.order.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(name = "OrderDto", description = "Represents an order with items and metadata")
public record OrderDto(

        @Schema(description = "Unique identifier for the order", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "UUID of the user who placed the order", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "Order status", example = "PENDING")
        String status,

        @Schema(description = "Total price of the order", example = "149.99")
        BigDecimal totalPrice,

        @Schema(description = "Date when the order was placed", example = "2026-01-25T19:12:54")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime orderDate,

        @Schema(description = "List of items included in the order")
        List<OrderItemDto> orderItems,

        @Schema(description = "Associated payment identifier", example = "987e6543-e21b-12d3-a456-426614174999")
        UUID paymentId

) {}
