package com.youssef.ecomera.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@RequiredArgsConstructor
@Schema(name = "OrderStatus", description = "Represents the lifecycle status of an order")
public enum OrderStatus {

    @Schema(description = "Order has been created but not yet processed")
    PENDING("Pending"),

    @Schema(description = "Order has been shipped to the customer")
    SHIPPED("Shipped"),

    @Schema(description = "Order has been delivered to the customer")
    DELIVERED("Delivered"),

    @Schema(description = "Order has been canceled")
    CANCELED("Canceled"),

    @Schema(description = "Order has been paid successfully")
    PAID("Paid");

    private final String statusName;
}