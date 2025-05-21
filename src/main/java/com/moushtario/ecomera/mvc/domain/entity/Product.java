package com.moushtario.ecomera.mvc.domain.entity;

import com.moushtario.ecomera.mvc.base.BaseEntity;
import com.moushtario.ecomera.mvc.domain.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0 CHECK (stock >= 0)")
    private int stock;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

}
