package com.youssef.ecomera.domain.order.entity;


import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an Order in the e-commerce application.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "\"orders\"")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime orderDate;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItem;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

}
