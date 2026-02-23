package com.youssef.ecomera.domain.cart.dto.cartitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update cart item quantity")
public record CartItemUpdateDto(
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 999, message = "Quantity cannot exceed 999")
        @Schema(description = "New quantity", example = "3", minimum = "1", maximum = "999")
        Integer quantity
) {}

