package com.youssef.ecomera.domain.payment.dto;

import com.youssef.ecomera.domain.payment.enums.PaymentMethod;
import com.youssef.ecomera.domain.payment.enums.PaymentStatus;
import lombok.Builder;

import com.youssef.ecomera.domain.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * Read DTO for {@link Payment}
 */

@Builder
@Schema(name = "PaymentDto", description = "Represents a payment with metadata")
public record PaymentDto(

        @Schema(description = "Unique identifier of the payment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Payment amount", example = "49.99")
        BigDecimal amount,

        @Schema(description = "Payment method used", example = "PAYPAL", implementation = PaymentMethod.class)
        PaymentMethod paymentMethod,

        @Schema(description = "Current status of the payment", example = "COMPLETED", implementation = PaymentStatus.class)
        PaymentStatus paymentStatus,

        @Schema(description = "Transaction identifier from external provider", example = "TXN-987654321")
        String transactionId,

        @Schema(description = "UUID of the order associated with this payment", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID orderId,

        @Schema(description = "Timestamp when the payment was created", example = "2026-01-25T19:12:54")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the payment was last updated", example = "2026-01-26T10:45:00")
        LocalDateTime updatedAt
) {
}
