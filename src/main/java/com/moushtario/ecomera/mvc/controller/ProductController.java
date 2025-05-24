package com.moushtario.ecomera.mvc.controller;

import com.moushtario.ecomera.mvc.domain.dto.ProductDto;
import com.moushtario.ecomera.mvc.service.ProductService;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDTO) {
        ProductDto product = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> productsPage = productService.getAllProducts(pageable);
        return ResponseEntity.ok(productsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        if(!productService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ProductDto product = productService.getProductById(id);
        return new ResponseEntity<> (product, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.countProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id, @RequestBody ProductDto productDTO) {
        if(!productService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        productDTO.setId(id);
        ProductDto updatedProduct = productService.saveProduct(productDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        if(!productService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    // Special Endpoint

    @GetMapping("/search")
    public  ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProducts(query, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);

    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDto> products = productService.getProductsByCategory(category, pageable);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/title")
    public ResponseEntity<ProductDto> getProductByTitle(@RequestParam String title) {
        ProductDto productDto = productService.getProductByTitle(title);
        if (productDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/price")
    public ResponseEntity<?> getProductsByPriceInRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) throws Exception {

        if (minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) > 0) {
            return ResponseEntity.badRequest().body("Invalid price range");
        }
        Pageable pageable = PageRequest.of(page, size);
        Iterable<ProductDto> products = productService.getProductsByPriceBetweenRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

}
