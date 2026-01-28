package com.youssef.ecomera.domain.order.dto.order;

import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
@Schema(name = "OrderCreateDto", description = "Payload for creating a new order")
public record OrderCreateDto(

        @NotNull
        @Schema(description = "UUID of the user placing the order", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @NotEmpty
        @Schema(description = "List of items included in the order")
        List<OrderItemCreateDto> items
) {
}
