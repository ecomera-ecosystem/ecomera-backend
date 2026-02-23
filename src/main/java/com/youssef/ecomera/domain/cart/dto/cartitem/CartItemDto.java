package com.youssef.ecomera.domain.cart.dto.cartitem;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Cart item response")
public record CartItemDto(
        @Schema(description = "Cart item ID", example = "123e4567-e89b-12d3-a456-426614174002")
        UUID id,

        @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174003")
        UUID productId,

        @Schema(description = "Product title", example = "Wireless Headphones")
        String productTitle,

        @Schema(description = "Product image URL", example = "https://example.com/image.jpg")
        String productImage,

        @Schema(description = "Quantity", example = "2")
        Integer quantity,

        @Schema(description = "Unit price when added to cart", example = "79.99")
        BigDecimal unitPrice,

        @Schema(description = "Subtotal (quantity * unit price)", example = "159.98")
        BigDecimal subtotal,

        @Schema(description = "Current product stock", example = "15")
        Integer availableStock
) {}
