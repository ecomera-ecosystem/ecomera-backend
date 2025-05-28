package com.moushtario.ecomera.mvc.domain.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderDto {

    private UUID id; // Unique identifier for the order
    private UUID userId; // ID of the user who placed the order
    private String status; // Order status (e.g., PENDING, SHIPPED, DELIVERED, CANCELED)
    private BigDecimal totalPrice; // Total price of the order
//    private String shippingAddress; // Address where the order will be shipped
//    private String paymentMethod; // Payment method used for the order

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime orderDate; // Date when the order was placed

    private List<OrderItemDto> orderItems;
}
