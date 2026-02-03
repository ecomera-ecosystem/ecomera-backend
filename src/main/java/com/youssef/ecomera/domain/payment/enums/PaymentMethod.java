package com.youssef.ecomera.domain.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Schema(name = "PaymentMethod", description = "Supported payment methods")
public enum PaymentMethod {

    @Schema(description = "Payment via PayPal")
    PAYPAL("PayPal"),

    @Schema(description = "Payment via credit card")
    CREDIT_CARD("Credit Card"),

    @Schema(description = "Payment via bank transfer")
    BANK_TRANSFER("Bank Transfer");

    @JsonValue
    private final String displayName;

    @JsonCreator
    public static PaymentMethod fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("PaymentMethod cannot be null");
        }

        return Arrays.stream(values())
                .filter(method -> method.name().equalsIgnoreCase(value) ||
                        method.displayName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid payment method: " + value + ". Valid values are: " +
                                Arrays.stream(values())
                                        .map(PaymentMethod::name)
                                        .collect(Collectors.joining(", "))
                ));
    }
}