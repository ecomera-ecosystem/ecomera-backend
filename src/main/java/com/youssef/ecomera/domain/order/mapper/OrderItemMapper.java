package com.youssef.ecomera.domain.order.mapper;

import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemUpdateDto;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // === 1. Entity -> DTO (Read) ===
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderItemDto toDto(OrderItem entity);

    // === 2. DTO -> Entity (Create) ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unitPrice", ignore = true) // will be set in service from Product
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "product", ignore = true) // will be set in service
    @Mapping(target = "order", ignore = true)   // will be set in service
    OrderItem toEntity(OrderItemCreateDto dto);

    // === 3. DTO -> Existing Entity (Update) ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unitPrice", ignore = true) // re-calculated
    @Mapping(target = "order", ignore = true)     // not changed on update
    @Mapping(target = "product", ignore = true)   // set manually in service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(OrderItemUpdateDto dto, @MappingTarget OrderItem entity);
}
