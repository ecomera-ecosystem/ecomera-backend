package com.youssef.ecomera.domain.payment.dto;

import com.youssef.ecomera.domain.payment.entity.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Create DTO for {@link Payment}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCreateDto {


//        @NotNull
//        @DecimalMin(value = "0.0", inclusive = false)
//        private BigDecimal amount;

        @NotNull
        private String paymentMethod; // Enum name in uppercase

        @NotNull
        private UUID orderId;

        private String transactionId;


}
