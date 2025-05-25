package com.moushtario.ecomera.mvc.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("Pending"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELED("Canceled"),
    PAID("Paid");

    private final String statusName;

//    public static OrderStatus fromString(String value) {
//        for (OrderStatus status : values()) {
//            if (status.name().equalsIgnoreCase(value) || status.getStatusName().equalsIgnoreCase(value)) {
//                return status;
//            }
//        }
//        throw new IllegalArgumentException("Unknown order status: " + value);
//    }


}
