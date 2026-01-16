package com.youssef.ecomera.domain.payment.dto;

import com.youssef.ecomera.domain.payment.entity.Payment;
import lombok.*;

import java.math.BigDecimal;

/**
 * Update DTO for {@link Payment}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentUpdateDto {

    private BigDecimal amount;

    private String paymentMethod;

    private String paymentStatus;

    private String transactionId;
}

