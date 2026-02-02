package com.youssef.ecomera.domain.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Schema(name = "PaymentMethod", description = "Supported payment methods")
public enum PaymentMethod {

    @Schema(description = "Payment via PayPal")
    PAYPAL("PayPal"),

    @Schema(description = "Payment via credit card")
    CREDIT_CARD("Credit card"),

    @Schema(description = "Payment via bank transfer")
    BANK_TRANSFER("Bank transfer");

    @JsonValue
    private final String methodName;

    public static Optional<PaymentMethod> fromString(String value) {
        return Arrays.stream(values())
                .filter(pm -> pm.name().equalsIgnoreCase(value) ||
                        pm.getMethodName().equalsIgnoreCase(value))
                .findFirst();
    }

    @JsonCreator
    public static PaymentMethod forValue(String value) {
        return fromString(value)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid payment method: " + value + ". Valid values are: " +
                                Arrays.stream(values())
                                        .map(PaymentMethod::getMethodName)
                                        .collect(Collectors.joining(", "))
                ));
    }
}
