package com.youssef.ecomera.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    private final String displayName;

    @JsonCreator
    public static PaymentStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("PaymentStatus cannot be null");
        }

        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value) ||
                        status.displayName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid payment status: " + value + ". Valid values are: " +
                                Arrays.stream(values())
                                        .map(PaymentStatus::name)
                                        .collect(Collectors.joining(", "))
                ));
    }
}