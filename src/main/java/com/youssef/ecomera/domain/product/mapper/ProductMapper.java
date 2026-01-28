package com.youssef.ecomera.domain.product.mapper;

import com.youssef.ecomera.domain.product.dto.ProductCreateDto;
import com.youssef.ecomera.domain.product.dto.ProductDto;
import com.youssef.ecomera.domain.product.dto.ProductUpdateDto;
import com.youssef.ecomera.domain.product.entity.Product;
import org.mapstruct.*;

import java.util.List;

/**
 * {@param componentModel} = "spring" allows Spring to manage the lifecycle of the mapper bean.
 * This means you can inject this mapper into other Spring components (like services or controllers) using @Autowired.
 *
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity → DTO
    ProductDto toDto(Product product);

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
    void updateProductFromDto(ProductUpdateDto dto, @MappingTarget Product product);

    // Collections
    List<ProductDto> toDtoList(List<Product> productList);
    Iterable<ProductDto> toDtoIterable(Iterable<Product> byPriceBetween);
}

