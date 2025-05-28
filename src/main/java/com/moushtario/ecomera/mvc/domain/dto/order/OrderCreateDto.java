package com.moushtario.ecomera.mvc.domain.dto.order;

import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemCreateDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderCreateDto {

    @NotNull
    private UUID userId;

    @NotEmpty
    private List<OrderItemCreateDto> items;
}
