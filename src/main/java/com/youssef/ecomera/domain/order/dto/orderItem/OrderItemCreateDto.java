package com.youssef.ecomera.domain.order.dto.orderItem;

import com.youssef.ecomera.domain.order.entity.OrderItem;
import lombok.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

/**
 * Read DTO for {@link OrderItem}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDto {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Order ID is required")
    private UUID orderId;
}
