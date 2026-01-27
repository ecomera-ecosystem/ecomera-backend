package com.youssef.ecomera.domain.order.dto.orderitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(name = "OrderItemUpdateDto", description = "Payload for updating an existing order item")
public record OrderItemUpdateDto(

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        @Schema(description = "Updated quantity of the product", example = "3")
        Integer quantity,

        @NotNull
        @Schema(description = "UUID of the product being updated", example = "987e6543-e21b-12d3-a456-426614174999")
        UUID productId
) {
}

