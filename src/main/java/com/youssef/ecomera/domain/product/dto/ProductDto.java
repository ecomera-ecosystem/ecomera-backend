package com.youssef.ecomera.domain.product.dto;

import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;


import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read DTO for {@link Product}
 */

@Builder
public record ProductDto(
        @Schema(description = "Unique product ID") UUID id,
        @Schema(description = "Product title") String title,
        @Schema(description = "Detailed description") String description,
        @Schema(description = "Image URL") String imageUrl,
        @Schema(description = "Price in USD") BigDecimal price,
        @Schema(description = "Available stock") Integer stock,
        @Schema(description = "Category type, e.g. ELECTRONICS") CategoryType category,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp") LocalDateTime updatedAt
) {
}

