package com.youssef.ecomera.domain.product.controller;

import com.youssef.ecomera.domain.product.dto.ProductCreateDto;
import com.youssef.ecomera.domain.product.dto.ProductDto;
import com.youssef.ecomera.domain.product.dto.ProductUpdateDto;
import com.youssef.ecomera.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product", description = "Creates a product and returns the created resource")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductCreateDto productDTO) {
        ProductDto product = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "Get paginated list of products", description = "Returns products with pagination and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated or invalid token"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"})) @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sortBy, direction));
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@Parameter(description = "Product UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Get product count", description = "Returns the total number of products. If a category is provided, returns the count for that category.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category provided")
    })
    @GetMapping("/count")
    public ResponseEntity<Long> getProductCount(
            @Parameter(description = "Category name to filter by", example = "ELECTRONICS")
            @RequestParam(required = false)
            String category
    ) {
        return ResponseEntity.ok(
                (category == null || category.isEmpty())
                        ? productService.countProducts()
                        : productService.countProductsByCategory(category)
        );
    }

    @Operation(summary = "Update product", description = "Updates an existing product by ID. Only provided fields will be updated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "Product UUID") @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateDto dto
    ) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @Operation(summary = "Delete product", description = "Deletes a product by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "Product UUID") @PathVariable UUID id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products", description = "Searches products by query string with pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search query")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @Parameter(description = "Search query string", example = "Laptop") @RequestParam String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @Operation(summary = "Get products by category", description = "Returns products filtered by category with pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category provided")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @Parameter(description = "Category name", example = "ELECTRONICS") @PathVariable String category,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }

    @Operation(summary = "Get product by title", description = "Fetches a product by its title.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/title")
    public ResponseEntity<ProductDto> getProductByTitle(
            @Parameter(description = "Product title", example = "MacBook Pro M3") @RequestParam String title) {
        return ResponseEntity.ok(productService.getProductByTitle(title));
    }

    @Operation(summary = "Get products by price range", description = "Returns products within the specified price range with pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid price range")
    })
    @GetMapping("/price")
    public ResponseEntity<Iterable<ProductDto>> getProductsByPriceInRange(
            @Parameter(description = "Minimum price", example = "100.00") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price", example = "2000.00") @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByPriceBetweenRange(minPrice, maxPrice, pageable));
    }
}
