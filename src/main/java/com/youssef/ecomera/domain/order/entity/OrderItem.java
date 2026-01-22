package com.youssef.ecomera.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youssef.ecomera.common.audit.BaseEntity;
import com.youssef.ecomera.domain.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_item")
public class OrderItem extends BaseEntity {

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Unit price must be a valid monetary amount")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity; // use Integer for @NotNull to work

    @NotNull(message = "Order reference is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @NotNull(message = "Product reference is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
