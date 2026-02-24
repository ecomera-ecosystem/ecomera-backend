package com.youssef.ecomera.domain.cart.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "CartCreateDto", description = "Request to add item to cart")
public record CartCreateDto(

        @NotNull(message = "Product ID is required")
        @Schema(description = "Product ID to add", example = "123e4567-e89b-12d3-a456-426614174003")
        UUID productId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 999, message = "Quantity cannot exceed 999")
        @Schema(description = "Quantity to add", example = "2", minimum = "1", maximum = "999")
        Integer quantity
) {
}
