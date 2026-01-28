package com.youssef.ecomera.domain.product.dto;

import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Create DTO for {@link Product}
 */
@Schema(description = "DTO for creating a new product")
public record ProductCreateDto(
        @NotBlank
        @Schema(description = "Product title", example = "MacBook Pro M3")
        String title,

        @Schema(description = "Detailed description of the product", example = "Latest Apple MacBook Pro with M3 chip")
        String description,

        @NotBlank
        @Schema(description = "Image URL of the product", example = "https://example.com/images/macbook.jpg")
        String imageUrl,

        @NotNull
        @Positive
        @Schema(description = "Price of the product", example = "1999.99")
        BigDecimal price,

        @Min(0) @Schema(description = "Available stock quantity", example = "50")
        Integer stock,

        @NotBlank
        @Schema(description = "Category of the product", example = "ELECTRONICS", allowableValues = {"ELECTRONICS", "MEN's CLOTHING", "WOMEN's CLOTHING", "JEWLERY"})
        CategoryType category
) {
}