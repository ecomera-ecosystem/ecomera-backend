package com.youssef.ecomera.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@RequiredArgsConstructor
@Schema(name = "PaymentStatus", description = "Possible statuses of a payment")
public enum PaymentStatus {

    @Schema(description = "Payment is pending")
    PENDING("Pending"),

    @Schema(description = "Payment completed successfully")
    COMPLETED("Completed"),

    @Schema(description = "Payment failed")
    FAILED("Failed"),

    @Schema(description = "Payment refunded")
    REFUNDED("Refunded");

    @JsonValue
    private final String statusName;
}

