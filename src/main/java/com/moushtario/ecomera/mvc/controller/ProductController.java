package com.moushtario.ecomera.mvc.controller;

import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product productDTO) {
        Product product = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        if(!productService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Product product = productService.getProductById(id);
        return new ResponseEntity<> (product, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getProductCount() {
        Long count = productService.countProducts();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product productDTO) {
        if(!productService.isExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        productDTO.setId(id);
        Product updatedProduct = productService.saveProduct(productDTO);
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
    public  ResponseEntity<List<Product>> searchProducts(@RequestParam String query){

        List<Product> products = productService.searchProducts(query);
        return new ResponseEntity<>(products, HttpStatus.OK);

    }

    @GetMapping("/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/name")
    public ResponseEntity<Product> getProductByName(@RequestParam String title) {
        Product product = productService.getProductByTitle(title);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/price")
    public ResponseEntity<List<Product>> getProductsByPriceInRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        Iterable<Product> products = productService.getProductsByPriceBetweenRange(minPrice, maxPrice);
        return ResponseEntity.ok((List<Product>) products);
    }

}
