package com.moushtario.ecomera.mvc.domain.dto.payment;

import com.moushtario.ecomera.mvc.domain.entity.Payment;
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

