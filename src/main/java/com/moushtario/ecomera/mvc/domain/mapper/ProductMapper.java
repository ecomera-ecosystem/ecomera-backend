package com.moushtario.ecomera.mvc.domain.mapper;

import com.moushtario.ecomera.mvc.domain.dto.product.ProductCreateDto;
import com.moushtario.ecomera.mvc.domain.dto.product.ProductDto;
import com.moushtario.ecomera.mvc.domain.dto.product.ProductUpdateDto;
import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.domain.enums.CategoryType;
//import org.mapstruct.InheritInverseConfiguration;
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
//    @InheritInverseConfiguration // this one makes id createdAt updatedAT null in get
    @Mapping(target = "category", source = "category.name") // Convert enum to string
    ProductDto toDto(Product product);

    // DTO → Entity
    @Mapping(target = "id", ignore = true)          // Ignored for creation
    @Mapping(target = "createdAt", ignore = true)   // Auto-populated
    @Mapping(target = "updatedAt", ignore = true)   // Auto-populated
    @Mapping(target = "category", expression = "java(mapCategory(dto.getCategory()))")
    Product toEntity(ProductCreateDto dto);

    // ProductUpdateDto → Existing Product (for patch partial update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", expression = "java(mapCategory(dto.getCategory()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductUpdateDto dto, @MappingTarget Product product);

    // Custom enum conversion
    default CategoryType mapCategory(String category) {
        return category != null ?
                CategoryType.valueOf(category.toUpperCase()) :
                null;
    }

    List<ProductDto> toDtoList(List<Product> productList);
    Iterable<ProductDto> toDtoIterable(Iterable<Product> byPriceBetween);
}

