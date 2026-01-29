package com.youssef.ecomera.domain.product.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.domain.product.dto.ProductCreateDto;
import com.youssef.ecomera.domain.product.dto.ProductDto;
import com.youssef.ecomera.domain.product.dto.ProductUpdateDto;
import com.youssef.ecomera.domain.product.entity.Product;
import org.mapstruct.*;


@Mapper(config = BaseMappingConfig.class)
public interface ProductMapper extends BaseMapper<Product, ProductDto> {

    // DTO → Entity (create)
    @Mapping(target = "id", ignore = true)          // Ignored for creation
    @Mapping(target = "createdAt", ignore = true)   // Auto-populated
    @Mapping(target = "updatedAt", ignore = true)   // Auto-populated
    @Mapping(target = "createdBy", ignore = true)   // Auto-populated
    @Mapping(target = "updatedBy", ignore = true)   // Auto-populated
    Product toEntity(ProductCreateDto dto);

    // ProductUpdateDto → Existing Product (patch partial update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget Product product);
}

