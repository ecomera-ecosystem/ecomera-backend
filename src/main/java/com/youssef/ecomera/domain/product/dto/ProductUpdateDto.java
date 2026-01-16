package com.youssef.ecomera.domain.product.dto;

import com.youssef.ecomera.domain.product.entity.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Update DTO for {@link Product}
 * Same fields but are optional instead
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductUpdateDto {

    private String title;

    private String description;

    private String imageUrl;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @Min(0)
    private Integer stock;

    private String category; // Acceptable as "ELECTRONICS", "BOOKS", etc.
}
