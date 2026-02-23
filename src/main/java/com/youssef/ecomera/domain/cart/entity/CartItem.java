package com.youssef.ecomera.domain.cart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.youssef.ecomera.common.audit.BaseEntity;
import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.domain.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(indexes = {
        @Index(name = "idx_cart_item_cart_id", columnList = "cart_id"),
        @Index(name = "idx_cart_item_product_id", columnList = "product_id"),
})
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull(message = "Cart item must belong to a cart")
    @JsonBackReference
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Cart item must reference a product")
    private Product product;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    private BigDecimal unitPrice;

    // Business logic methods
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void increaseQuantity(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity - amount < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        this.quantity -= amount;
    }

    @PreUpdate
    @PrePersist
    private void validateStock() {
        if (product != null && product.getStock() != null && quantity > product.getStock()) {
            throw new BusinessException("Quantity exceeds available stock");
        }
    }
}