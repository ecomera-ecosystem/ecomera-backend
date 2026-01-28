package com.youssef.ecomera.domain.payment.dto;

import com.youssef.ecomera.domain.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
@Schema(name = "PaymentCreateDto", description = "Payload for creating a new payment")
public record PaymentCreateDto(

        @NotNull(message = "Amount is required")
        @Schema(description = "Payment amount", example = "49.99")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        @Schema(description = "Payment method (enum name in uppercase)", example = "PAYPAL", implementation = PaymentMethod.class)
        PaymentMethod paymentMethod,

        @NotNull(message = "Order ID is required")
        @Schema(description = "UUID of the order associated with this payment", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID orderId,

        @Schema(description = "Optional transaction identifier from external provider", example = "TXN-987654321")
        String transactionId
) {
}
