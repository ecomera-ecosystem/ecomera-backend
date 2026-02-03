package com.youssef.ecomera.domain.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Schema(name = "OrderStatus", description = "Represents the lifecycle status of an order")
public enum OrderStatus {

    @Schema(description = "Order created, awaiting payment")
    PENDING("Pending"),

    @Schema(description = "Payment received, order being prepared")
    PROCESSING("Processing"),

    @Schema(description = "Order is being packed and prepared for shipment")
    CONFIRMED("Confirmed"),

    @Schema(description = "Order has been shipped to the customer")
    SHIPPED("Shipped"),

    @Schema(description = "Order has been delivered to the customer")
    DELIVERED("Delivered"),

    @Schema(description = "Order has been canceled")
    CANCELED("Canceled");

    @JsonValue
    private final String statusName;

    public static Optional<OrderStatus> fromString(String value) {
        return Arrays.stream(values())
                .filter(os -> os.name().equalsIgnoreCase(value) || os.getStatusName().equalsIgnoreCase(value))
                .findFirst();
    }

    @JsonCreator
    public static OrderStatus forValue(String value) {
        return fromString(value)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid category: " + value + ". Valid values are: " +
                                Arrays.stream(values())
                                        .map(OrderStatus::getStatusName)
                                        .collect(Collectors.joining(", "))
                ));
    }
}