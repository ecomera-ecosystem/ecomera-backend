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
    public ProductDto saveProduct(ProductCreateDto dto) {
        Product product = productMapper.toEntity(dto);
        Product savedProduct = productRepository.save(product);

        log.info("Product created: {} - {}", savedProduct.getId(), savedProduct.getTitle());
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto update(UUID id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", id));

        productMapper.updateEntityFromDto(dto, product);
        Product updated = productRepository.save(product);

        log.info("Product updated: {}", id);
        return productMapper.toDto(updated);
    }

    public ProductDto getProductById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", id));
    }

    public Page<ProductDto> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(productMapper::toDto);
    }

    @Transactional
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

    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("Search query cannot be empty");
        }

        Page<Product> products = productRepository.searchProducts(query, pageable);
        return products.map(productMapper::toDto);
    }

    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        CategoryType categoryType = CategoryType.fromString(category)
                .orElseThrow(() -> new BusinessException("Invalid category: " + category));

        log.info("Searching products by category: {}", categoryType);
        Page<Product> products = productRepository.findByCategory(categoryType, pageable);

        return products.map(productMapper::toDto);
    }

    public ProductDto getProductByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException("Title cannot be null or empty");
        }

        Product product = productRepository.findByTitle(title);
        if (product == null) {
            throw new ResourceNotFoundException(getClass().getSimpleName(), "title", title);
        }

        return productMapper.toDto(product);
    }

    public Page<ProductDto> getProductsByPriceBetweenRange(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        if (minPrice == null || maxPrice == null) {
            throw new BusinessException("Price range cannot be null");
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException("Min price cannot be greater than max price");
        }

        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return products.map(productMapper::toDto);
    }
}