package com.youssef.ecomera.domain.order.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.domain.order.dto.order.OrderCreateDto;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.enums.OrderStatus;

import org.mapstruct.*;

import java.util.List;

@Mapper(config = BaseMappingConfig.class, uses = { OrderItemMapper.class })
public interface OrderMapper extends BaseMapper<Order, OrderDto> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdAt", target = "orderDate")
    @Mapping(source = "payment.id", target = "paymentId")

    OrderDto toDto(Order order);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "orderItems", ignore = true)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Order toEntity(OrderDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", constant = "PENDING") // default status
    Order toEntity(OrderCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(OrderUpdateDto dto, @MappingTarget Order order);


    // Custom enum conversion to map String to OrderStatus
    default OrderStatus mapStatus(String order) {
        return order != null ?
                OrderStatus.valueOf(order.toUpperCase()) :
                null;
    }

    // Handle orderItems manually after mapping
    @AfterMapping
    default void mapOrderItems(Order order, @MappingTarget OrderDto.OrderDtoBuilder dtoBuilder) {
        if (order.getOrderItems() != null) {
            List<OrderItemDto> items = order.getOrderItems().stream()
                    .map(item -> OrderItemDto.builder()
                            .id(item.getId())
                            .productId(item.getProduct().getId())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .createdAt(item.getCreatedAt())
                            .updatedAt(item.getUpdatedAt())
                            .build())
                    .toList();
            dtoBuilder.orderItems(items);
        }
    }
}
