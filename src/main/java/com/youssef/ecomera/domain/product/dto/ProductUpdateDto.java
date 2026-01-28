package com.youssef.ecomera.domain.product.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;

/**
 * Update DTO for {@link Product}
 * Same fields but are optional instead
 */
@Schema(description = "DTO for updating an existing product. All fields are optional.")
public record ProductUpdateDto(
        @Schema(description = "Updated product title", example = "MacBook Pro M3 - 16 inch")
        String title,

        @Schema(description = "Updated description", example = "Updated specs and details")
        String description,

        @Schema(description = "Updated image URL", example = "https://example.com/images/macbook-new.jpg")
        String imageUrl,

        @PositiveOrZero
        @Schema(description = "Updated price", example = "2099.99")
        BigDecimal price,

        @Min(0)
        @Schema(description = "Updated stock quantity", example = "30")
        Integer stock,

        @Schema(description = "Updated Category of the product", example = "ELECTRONICS", allowableValues = {"ELECTRONICS", "MEN's CLOTHING", "WOMEN's CLOTHING", "JEWLERY"})
        CategoryType category
) {
}
