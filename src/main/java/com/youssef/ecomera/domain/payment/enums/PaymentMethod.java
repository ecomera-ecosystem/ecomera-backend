package com.youssef.ecomera.domain.payment.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

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
}
