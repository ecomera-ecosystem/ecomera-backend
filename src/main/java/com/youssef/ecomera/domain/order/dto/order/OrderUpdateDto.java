package com.youssef.ecomera.domain.order.dto.order;


import com.youssef.ecomera.domain.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "OrderUpdateDto", description = "Payload for updating an existing order")
public record OrderUpdateDto(

        @Schema(description = "New status for the order", example = "SHIPPED", implementation = OrderStatus.class)
        OrderStatus status
) {}
