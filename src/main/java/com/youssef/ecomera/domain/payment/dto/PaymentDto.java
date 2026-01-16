package com.youssef.ecomera.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.youssef.ecomera.domain.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read DTO for {@link Payment}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

    private UUID id;

    private BigDecimal amount;

    private String paymentMethod;

    private String paymentStatus;

    private String transactionId;

    private UUID orderId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

