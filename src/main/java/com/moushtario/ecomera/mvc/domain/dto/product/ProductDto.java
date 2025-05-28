package com.moushtario.ecomera.mvc.domain.dto.product;

import com.moushtario.ecomera.mvc.domain.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read DTO for {@link Product}
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private int stock;
    private String category; // Will map to/from CategoryType enum

    // Optional: For better API responses
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
