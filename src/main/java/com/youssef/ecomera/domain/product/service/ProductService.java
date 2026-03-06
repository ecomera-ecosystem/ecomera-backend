package com.youssef.ecomera.domain.product.service;

import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.product.dto.ProductCreateDto;
import com.youssef.ecomera.domain.product.dto.ProductDto;
import com.youssef.ecomera.domain.product.dto.ProductUpdateDto;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.enums.CategoryType;
import com.youssef.ecomera.domain.product.mapper.ProductMapper;
import com.youssef.ecomera.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    @Caching(
            put = @CachePut(value = "products", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "products-page", allEntries = true),
                    @CacheEvict(value = "products-search", allEntries = true),
                    @CacheEvict(value = "products-category", allEntries = true),
                    @CacheEvict(value = "products-title", allEntries = true)
            }
    )
    public ProductDto saveProduct(ProductCreateDto dto) {
        ProductCreateDto sanitized = ProductCreateDto.builder()
                .title(dto.title())
                .description(dto.description())
                .imageUrl(dto.imageUrl())
                .price(dto.price())
                .stock(dto.stock())
                .category(dto.category())
                .build();

        Product product = productMapper.toEntity(sanitized);
        Product savedProduct = productRepository.save(product);
        log.info("Product created: {} - {}", savedProduct.getId(), savedProduct.getTitle());
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "products", key = "#id"),
            evict = {
                    @CacheEvict(value = "products-page", allEntries = true),
                    @CacheEvict(value = "products-search", allEntries = true),
                    @CacheEvict(value = "products-category", allEntries = true),
                    @CacheEvict(value = "products-title", allEntries = true)
            }
    )
    public ProductDto update(UUID id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", id));
        productMapper.updateEntityFromDto(dto, product);
        Product updated = productRepository.save(product);
        log.info("Product updated: {}", id);
        return productMapper.toDto(updated);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductDto getProductById(UUID id) {
        log.debug("Cache miss fetching product {} from DB", id);
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", id));
    }

    @Cacheable(value = "products-page", key = "#page + '-' + #size + '-' + #sortBy + '-' + #direction")
    public Page<ProductDto> getAllProducts(int page, int size, String sortBy, String direction) {
        log.debug("Cache miss fetching all products from DB");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products-page", allEntries = true),
            @CacheEvict(value = "products-search", allEntries = true),
            @CacheEvict(value = "products-category", allEntries = true),
            @CacheEvict(value = "products-title", allEntries = true)
    })
    public void deleteProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", id));
        productRepository.delete(product);
        log.info("Product deleted: {}", id);
    }

    public long countProducts() {
        return productRepository.count();
    }

    public long countProductsByCategory(String category) {
        CategoryType categoryType = CategoryType.fromString(category)
                .orElseThrow(() -> new BusinessException("Invalid category: " + category));
        return productRepository.countProductsByCategory(categoryType);
    }

    @Cacheable(value = "products-search", key = "#query + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("Search query cannot be empty");
        }
        log.debug("Cache miss - searching products for query: {}", query);
        return productRepository.searchProducts(query, pageable).map(productMapper::toDto);
    }

    @Cacheable(value = "products-category", key = "#category + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        CategoryType categoryType = CategoryType.fromString(category)
                .orElseThrow(() -> new BusinessException("Invalid category: " + category));
        log.info("Cache miss - fetching products by category: {}", categoryType);
        return productRepository.findByCategory(categoryType, pageable).map(productMapper::toDto);
    }

    @Cacheable(value = "products-title", key = "#title")
    public ProductDto getProductByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException("Title cannot be null or empty");
        }
        log.debug("Cache miss - fetching product by title: {}", title);
        Product product = productRepository.findByTitle(title);
        if (product == null) {
            throw new ResourceNotFoundException(getClass().getSimpleName(), "title", title);
        }
        return productMapper.toDto(product);
    }

    public Page<ProductDto> getProductsByPriceBetweenRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice == null || maxPrice == null) {
            throw new BusinessException("Price range cannot be null");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException("Min price cannot be greater than max price");
        }
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable).map(productMapper::toDto);
    }
}