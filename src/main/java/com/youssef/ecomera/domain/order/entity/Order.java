package com.youssef.ecomera.domain.order.entity;


import com.youssef.ecomera.common.audit.BaseEntity;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "\"order\"")
public class Order extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @NotNull
    @DecimalMin(value = "0.00", message = "Total price must be positive")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public void recalculateTotal() {
        this.totalPrice = orderItems.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Helper for adding items
    public void addItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    // Helper for removing items
    public void removeItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }
}
