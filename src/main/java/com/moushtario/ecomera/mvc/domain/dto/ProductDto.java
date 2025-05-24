package com.moushtario.ecomera.mvc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.moushtario.ecomera.mvc.domain.entity.Product}
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {
    private UUID id;         // Matches BaseEntity
    private String title;    // Matches Product.title
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private int stock;
    private String category; // Will map to/from CategoryType enum

    // Optional: For better API responses
    private LocalDateTime createdAt; // From BaseEntity
    private LocalDateTime updatedAt; // From BaseEntity

}
