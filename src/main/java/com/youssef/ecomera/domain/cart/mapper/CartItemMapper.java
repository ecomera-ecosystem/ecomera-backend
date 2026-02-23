package com.youssef.ecomera.domain.cart.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.domain.cart.dto.cartitem.CartItemDto;
import com.youssef.ecomera.domain.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMappingConfig.class)
public interface CartItemMapper extends BaseMapper<CartItem, CartItemDto> {
    @Override
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "productImage", source = "product.imageUrl")
    @Mapping(target = "subtotal", expression = "java(entity.getSubtotal())")
    @Mapping(target = "availableStock", source = "product.stock")
    CartItemDto toDto(CartItem entity);
}