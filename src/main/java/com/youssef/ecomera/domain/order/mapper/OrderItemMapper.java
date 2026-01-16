package com.youssef.ecomera.domain.order.mapper;

import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemDto;
import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemUpdateDto;
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
    @Mapping(target = "product", ignore = true) // will be set in service
    @Mapping(target = "order", ignore = true)   // will be set in service
    OrderItem toEntity(OrderItemCreateDto dto);

    // === 3. DTO -> Existing Entity (Update) ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unitPrice", ignore = true) // re-calculated
    @Mapping(target = "order", ignore = true)     // not changed on update
    @Mapping(target = "product", ignore = true)   // set manually in service
    void updateEntityFromDto(OrderItemUpdateDto dto, @MappingTarget OrderItem entity);


    // OrderItem - Order conversion methods

    // Extract Order record from an order_id
//    default Order toOrder(UUID orderId) {
//        if (orderId == null) {
//            return null;
//        }
//        Order order = new Order();
//        order.setId(orderId);
//        return order;
//    }
//    // Extract a order_id from an Order object
//    default UUID toOrderId(Order order) {
//        return (order != null) ? order.getId() : null;
//    }
//
//    // OrderItem - Product conversion methods
//
//    // Extract Product record from a product_id
//    default Product toProduct(UUID productId) {
//        if (productId == null) {
//            return null;
//        }
//        Product product = new Product();
//        product.setId(productId);
//        return product;
//    }
//
//    // Extract a product_id from a Product object
//    default UUID toProductId(Product product) {
//        return (product != null) ? product.getId() : null;
//    }


}
