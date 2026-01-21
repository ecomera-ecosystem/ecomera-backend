package com.youssef.ecomera.domain.order.mapper;

import com.youssef.ecomera.domain.order.dto.order.OrderCreateDto;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.user.entity.User;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", constant = "PENDING") // default status
    Order toEntity(OrderCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(OrderUpdateDto dto, @MappingTarget Order order);


    // Custom enum conversion to map String to OrderStatus
    default OrderStatus mapStatus(String order) {
        return order != null ?
                OrderStatus.valueOf(order.toUpperCase()) :
                null;
    }
    // Order - User conversion methods
    // Get User object from a user_id
    default User toUser(UUID userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
    // Extract a user_id from a user object
    default UUID toUserId(User user){
        return user!= null ? user.getId() : null;
    }
}
