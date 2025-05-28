package com.moushtario.ecomera.mvc.domain.mapper;

import com.moushtario.ecomera.mvc.domain.dto.order.OrderCreateDto;
import com.moushtario.ecomera.mvc.domain.dto.order.OrderDto;
import com.moushtario.ecomera.mvc.domain.dto.order.OrderUpdateDto;
import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.enums.OrderStatus;
import com.moushtario.ecomera.user.User;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItem", target = "orderItems")
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
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
