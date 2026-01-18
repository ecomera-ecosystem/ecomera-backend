package com.youssef.ecomera.domain.product.service;

import com.youssef.ecomera.domain.product.dto.ProductCreateDto;
import com.youssef.ecomera.domain.product.dto.ProductUpdateDto;
import com.youssef.ecomera.domain.product.mapper.ProductMapper;
import com.youssef.ecomera.domain.product.dto.ProductDto;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;
import com.youssef.ecomera.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public ProductDto saveProduct(ProductCreateDto dto) {
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


    public ProductDto update(UUID id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        productMapper.updateProductFromDto(dto, product);

        Product updated = productRepository.save(product);
        return productMapper.toDto(updated);
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
    public Page<ProductDto> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toDto);
    }

    public void deleteProductById(UUID id) {
        productRepository.deleteById(id);
    }

    public long countProducts() {
        return productRepository.count();
    }

    public long countProductsByCategory(String category) {
        CategoryType categoryType = CategoryType.fromString(category)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + category));

        return productRepository.countProductsByCategory(categoryType);
    }

    // Search
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        Page<Product> products =  productRepository.searchProducts(query, pageable);
        return products.map(productMapper::toDto);
    }

    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        CategoryType categoryType = CategoryType.fromString(category)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + category));

        log.info("Searching products by category: {}", categoryType);

        Page<Product> products = productRepository.findByCategory(categoryType, pageable);
        return products.map(productMapper::toDto);
    }

    public ProductDto getProductByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        return productMapper.toDto(productRepository.findByTitle(title));
    }

    public Page<ProductDto> getProductsByPriceBetweenRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) throws Exception {
        if (minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) > 0) {
            throw new Exception("Invalid price range");
        }
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return products.map(productMapper::toDto);
    }
}
