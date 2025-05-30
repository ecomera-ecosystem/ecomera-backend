package com.moushtario.ecomera.mvc.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentMethod {

    PAYPAL("Paypal"),
    CREDIT_CARD("Credit card"),
    BANK_TRANSFER("Bank transfer");

    private final String methodName;
}
