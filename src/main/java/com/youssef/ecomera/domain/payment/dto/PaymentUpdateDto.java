package com.youssef.ecomera.domain.payment.dto;

import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.enums.PaymentMethod;
import com.youssef.ecomera.domain.payment.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Update DTO for {@link Payment}
 */
@Schema(name = "PaymentUpdateDto", description = "Payload for updating an existing payment")
@Builder
public record PaymentUpdateDto(

        @Schema(description = "Updated payment amount", example = "59.99")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @Schema(description = "Updated payment method", example = "CREDIT_CARD", implementation = PaymentMethod.class)
        PaymentMethod paymentMethod,

        @Schema(description = "Updated payment status", example = "REFUNDED", implementation = PaymentStatus.class)
        PaymentStatus paymentStatus,

        @Schema(description = "Updated transaction identifier", example = "TXN-123456789")
        @Size(max = 255, message = "Transaction ID must not exceed 255 characters")
        String transactionId
) {
}