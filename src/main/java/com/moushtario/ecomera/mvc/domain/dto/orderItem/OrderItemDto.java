package com.moushtario.ecomera.mvc.domain.dto.orderItem;

import com.moushtario.ecomera.mvc.domain.entity.OrderItem;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read DTO for {@link OrderItem}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto implements Serializable {
    UUID id;
    BigDecimal unitPrice;
    Integer quantity;
    UUID orderId;
    UUID productId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}