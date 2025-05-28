package com.moushtario.ecomera.mvc.domain.dto.product;

import com.moushtario.ecomera.mvc.domain.entity.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create DTO for {@link Product}
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductCreateDto {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String imageUrl;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Min(0)
    private Integer stock;

    @NotBlank
    private String category; // Acceptable as "ELECTRONICS", "BOOKS", etc.
}
