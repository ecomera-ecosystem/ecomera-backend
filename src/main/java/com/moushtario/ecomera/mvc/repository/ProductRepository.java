package com.moushtario.ecomera.mvc.repository;

import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.domain.enums.CategoryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(""" 
            SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%'))
            """
    )
    Page<Product> searchProducts(@Param("query") String query,
                                 Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category")
    Page<Product> findByCategory(@Param("category") CategoryType category,
                                 Pageable pageable);

    Product findByTitle(String title);

    Page<Product> findByPriceBetween(
            // First arg: Min price
            @NotNull(message = "Minimum price is required")
            @DecimalMin(value = "0.0", inclusive = false, message = "The minimum price must be greater than zero")
            @Digits(integer = 10, fraction = 2,
                    message = "Minimum price must have up to 10 digits and 2 decimal places") BigDecimal minPrice,
            // Second arg: Max price
            @NotNull(message = "Price is required")
            @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
            @Digits(integer = 10, fraction = 2,
                    message = "The maximum Price must have up to 10 digits and 2 decimal places") BigDecimal maxPrice,
            Pageable pageable);
}

