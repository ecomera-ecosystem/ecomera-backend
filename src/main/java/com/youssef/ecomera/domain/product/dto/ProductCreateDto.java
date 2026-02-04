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
        @NotBlank(message = "Product title is required and cannot be blank")
        @Schema(description = "Product title", example = "MacBook Pro M3")
        String title,

        @Schema(description = "Detailed description of the product",
                example = "Latest Apple MacBook Pro with M3 chip")
        String description,

        @NotBlank(message = "Image URL is required and cannot be blank")
        @Schema(description = "Image URL of the product",
                example = "https://example.com/images/macbook.jpg")
        String imageUrl,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        @Schema(description = "Price of the product", example = "1999.99")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        @Schema(description = "Available stock quantity", example = "50")
        Integer stock,

        @NotNull(message = "Category is required")
        @Schema(description = "Category of the product", example = "ELECTRONICS", allowableValues = {"ELECTRONICS", "JEWELRY", "MENS_CLOTHING", "WOMENS_CLOTHING"})
        CategoryType category
) {
}