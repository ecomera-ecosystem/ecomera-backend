package com.moushtario.ecomera.mvc.service;

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

    // JPA methods
    public Boolean isExists(UUID id) {
        return productRepository.existsById(id);
    }

    public Product saveProduct(Product p) {
        // Convert ProductDTO to Product entity
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
        return productRepository.save(p);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Get all products
     * Transaction tells Hibernate/JPA this transaction won't modify data
     * And allows for database-level optimizations (some DBs optimize read-only queries)
     * @return List of products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public void deleteProductById(UUID id) {
        productRepository.deleteById(id);
    }

    public long countProducts() {
        return productRepository.count();
    }

    // Search
    public List<Product> searchProducts(String query) {
        return  productRepository.searchProducts(query);
    }

    public List<Product> getProductsByCategory(String category) {
        CategoryType categoryType = CategoryType.valueOf(category);

        log.info("Searching products by category: " + categoryType);

        return productRepository.findByCategory(categoryType);
    }

    public Product getProductByTitle(String title) {
        return productRepository.findByTitle(title);
    }

    public Iterable<Product> getProductsByPriceBetweenRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // TODO: Pagination
    //    public Page<Product> findProductsByCategory(String category, Pageable pageable) {
    //        return productRepository.findProductsByCategory(CategoryType.valueOf(category), pageable);
    //    }

}
