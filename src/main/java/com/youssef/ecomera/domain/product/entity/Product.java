package com.youssef.ecomera.domain.product.entity;

import com.youssef.ecomera.domain.product.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
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
