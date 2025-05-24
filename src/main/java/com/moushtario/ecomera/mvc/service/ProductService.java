package com.moushtario.ecomera.mvc.service;

import com.moushtario.ecomera.mapper.ProductMapper;
import com.moushtario.ecomera.mvc.domain.dto.ProductDto;
import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.domain.enums.CategoryType;
import com.moushtario.ecomera.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // JPA methods
    public Boolean isExists(UUID id) {
        return productRepository.existsById(id);
    }

    public ProductDto saveProduct(ProductDto dto) {
        /*
        // Convert ProductDto to Product entity
//        Product product = Product.builder()
//                .title(p.getTitle())
//                .description(p.getDescription())
//                .price(p.getPrice())
////                .category(CategoryType.valueOf(String.valueOf(p.getCategory()))) // CategoryType
//                .category(Category.builder()
//                        .name(p.getCategory())
//                        .description(p.getCategory().getName())
//                        .build())
//                .imageUrl(p.getImageUrl())
//                .build();
*/
        Product product = productMapper.toEntity(dto);
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto getProductById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /**
     * Get all products
     * Transaction tells Hibernate/JPA this transaction won't modify data
     * And allows for database-level optimizations (some DBs optimize read-only queries)
     * @return List of products
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts(){
        List<Product> productList = productRepository.findAll();
        return productMapper.toDtoList(productList);
    }

    public void deleteProductById(UUID id) {
        productRepository.deleteById(id);
    }

    public long countProducts() {
        return productRepository.count();
    }

    // Search
    public List<ProductDto> searchProducts(String query) {
        return  productMapper.toDtoList(productRepository.searchProducts(query));
    }

    public List<ProductDto> getProductsByCategory(String category) {
        CategoryType categoryType = CategoryType.fromString(category)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + category));

        log.info("Searching products by category: {}", categoryType);

        return productMapper.toDtoList(productRepository.findByCategory(categoryType));
    }

    public ProductDto getProductByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        return productMapper.toDto(productRepository.findByTitle(title));
    }

    public Iterable<ProductDto> getProductsByPriceBetweenRange(BigDecimal minPrice, BigDecimal maxPrice) throws Exception {
        if (minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) > 0) {
            throw new Exception("Invalid price range");
        }
        return productMapper.toDtoIterable(productRepository.findByPriceBetween(minPrice, maxPrice));
    }


    // TODO: Pagination
    //    public Page<Product> findProductsByCategory(String category, Pageable pageable) {
    //        return productRepository.findProductsByCategory(CategoryType.valueOf(category), pageable);
    //    }

}
