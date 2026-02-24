package com.youssef.ecomera.domain.cart.entity;

import com.youssef.ecomera.common.audit.BaseEntity;
import com.youssef.ecomera.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(indexes = {
        @Index(name = "idx_cart_user_id", columnList = "user_id")
})
public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "Cart must belong to a user")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Helper methods for cart operations
    public void addItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
        updateExpiration();
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
        updateExpiration();
    }

    public void clearItems() {
        cartItems.clear();
        updateExpiration();
    }

    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    private void updateExpiration() {
        this.expiresAt = LocalDateTime.now().plusDays(30);
    }

    @PrePersist
    @PreUpdate
    private void setExpiration() {
        if (this.expiresAt == null) {
            updateExpiration();
        }
    }
}