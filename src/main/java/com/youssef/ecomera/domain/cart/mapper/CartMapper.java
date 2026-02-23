package com.youssef.ecomera.domain.cart.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.domain.cart.dto.cart.CartDto;
import com.youssef.ecomera.domain.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMappingConfig.class, uses = CartItemMapper.class)
public interface CartMapper extends BaseMapper<Cart, CartDto> {
    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "totalPrice", expression = "java(entity.getTotalPrice())")
    @Mapping(target = "totalItems", expression = "java(entity.getTotalItems())")
    CartDto toDto(Cart entity);
}
