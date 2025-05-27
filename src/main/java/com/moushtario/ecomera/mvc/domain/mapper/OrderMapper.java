package com.moushtario.ecomera.mvc.domain.mapper;

import com.moushtario.ecomera.mvc.domain.dto.OrderDto;
import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.enums.OrderStatus;
import com.moushtario.ecomera.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "updatedAt", ignore = true)   // Auto-populated
    @Mapping(target = "status", expression = "java(mapStatus(orderDto.getStatus()))")
    @Mapping(source = "userId", target = "user")
    @Mapping(target = "orderDate" ) // Auto-populated
    Order toEntity(OrderDto orderDto);

    @Mapping(source = "user", target = "userId") // Map user entity to userId
    OrderDto toDto(Order order);


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
