package com.youssef.ecomera.domain.order.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemUpdateDto;
import com.youssef.ecomera.domain.order.entity.OrderItem;

import org.mapstruct.*;


@Mapper(config = BaseMappingConfig.class)
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemDto> {

    // === 1. Entity -> DTO (Read) ===
    @Override
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderItemDto toDto(OrderItem entity);

    // === 2. DTO -> Entity (Create) ===

    @Override
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    OrderItem toEntity(OrderItemDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @Mapping(target = "unitPrice", ignore = true) // will be set in service from Product
    @Mapping(target = "order.id", source = "orderId") //, ignore = true)   // will be set in service
    @Mapping(target = "product.id", source = "productId") //, ignore = true) // will be set in service
    OrderItem toEntity(OrderItemCreateDto dto);

    // === 3. DTO -> Existing Entity (Update) ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @Mapping(target = "order", ignore = true)     // not changed on update
    @Mapping(target = "product", ignore = true)   // set manually in service
    @Mapping(target = "unitPrice", ignore = true) // re-calculated
    void updateEntityFromDto(OrderItemUpdateDto dto, @MappingTarget OrderItem entity);
}
