package com.youssef.ecomera.domain.cart.dto.cart;

import com.youssef.ecomera.domain.cart.dto.cartitem.CartItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Shopping cart response")
public record CartDto(
        @Schema(description = "Cart ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "User ID who owns the cart", example = "123e4567-e89b-12d3-a456-426614174001")
        UUID userId,

        @Schema(description = "Items in the cart")
        List<CartItemDto> cartItems,

        @Schema(description = "Total price of all items", example = "159.97")
        BigDecimal totalPrice,

        @Schema(description = "Total number of items", example = "5")
        Integer totalItems,

        @Schema(description = "Cart expiration date", example = "2026-03-08T10:15:30")
        LocalDateTime expiresAt,

        @Schema(description = "Cart creation date", example = "2026-02-06T10:15:30")
        LocalDateTime createdAt,

        @Schema(description = "Cart last update date", example = "2026-02-06T10:15:30")
        LocalDateTime updatedAt
) {}
