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
import com.youssef.ecomera.utils.TestSuiteUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductService productService;

    private Product product;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        product = TestSuiteUtils.createProduct();
        products = TestSuiteUtils.createProductList();
    }

    // === Create =================================================================

    @Test
    void shouldSaveProductSuccessfully() {
        ProductCreateDto createDto = TestSuiteUtils.createProductCreateDtoFromProduct(product);
        ProductDto expectedDto = TestSuiteUtils.createProductDtoFromProduct(product);

        given(productMapper.toEntity(createDto)).willReturn(product);
        given(productRepository.save(product)).willReturn(product);
        given(productMapper.toDto(product)).willReturn(expectedDto);

        ProductDto saved = productService.saveProduct(createDto);

        assertThat(saved).isNotNull();
        assertThat(saved.title()).isEqualTo(product.getTitle());
        verify(productRepository, times(1)).save(product);
    }

    // === Read ====================================================================

    @Test
    void shouldGetProductByIdSuccessfully() {
        ProductDto expectedDto = TestSuiteUtils.createProductDtoFromProduct(product);

        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(productMapper.toDto(product)).willReturn(expectedDto);

        ProductDto actual = productService.getProductById(product.getId());

        assertThat(actual).isNotNull();
        assertThat(actual.title()).isEqualTo(product.getTitle());
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void shouldThrowWhenProductNotFoundById() {
        // Given
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("id");

        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void shouldGetProductByTitleSuccessfully() {
        //Given
        ProductDto expectedDto = TestSuiteUtils.createProductDtoFromProduct(product);

        given(productRepository.findByTitle(product.getTitle())).willReturn(product);
        given(productMapper.toDto(product)).willReturn(expectedDto);

        // When
        ProductDto actual = productService.getProductByTitle(product.getTitle());

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(product.getId());
        verify(productRepository, times(1)).findByTitle(product.getTitle());
    }

    @Test
    void shouldThrowWhenProductNotFoundByTitle() {
        // Given
        given(productRepository.findByTitle(any(String.class))).willThrow(ResourceNotFoundException.class);

        // When & Then
        assertThatThrownBy(() -> productService.getProductByTitle(product.getTitle()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, times(1)).findByTitle(product.getTitle());
    }

    @Test
    void shouldSearchProductsSuccessfully() {
        // Given
        String query = "laptop";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        given(productRepository.searchProducts(query, pageable)).willReturn(productPage);
        given(productMapper.toDto(any(Product.class))).willReturn(TestSuiteUtils.createProductDtoFromProduct(products.get(0)));

        // When
        Page<ProductDto> actual = productService.searchProducts(query, pageable);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSize(products.size());
        verify(productRepository, times(1)).searchProducts(query, pageable);
    }

    @Test
    void shouldThrowWhenSearchQueryIsEmpty() {
        assertThatThrownBy(() -> productService.searchProducts("", PageRequest.of(0, 10)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Search query cannot be empty");
    }

    @Test
    void shouldThrowWhenSearchQueryIsNull() {
        assertThatThrownBy(() -> productService.searchProducts(null, PageRequest.of(0, 10)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Search query cannot be empty");
    }

    @Test
    void shouldGetProductsByCategorySuccessfully() {
        // Given
        String category = "Electronics";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        given(productRepository.findByCategory(CategoryType.ELECTRONICS, pageable)).willReturn(productPage);
        given(productMapper.toDto(any(Product.class))).willReturn(TestSuiteUtils.createProductDtoFromProduct(products.get(0)));

        // When
        Page<ProductDto> actual = productService.getProductsByCategory(category, pageable);


        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSize(products.size());
        verify(productRepository, times(1)).findByCategory(CategoryType.ELECTRONICS, pageable);

    }

    @Test
    void shouldThrowWhenCategoryIsInvalid() {
        // Given
        String invalidCategory = "INVALID_CATEGORY";
        Pageable pageable = PageRequest.of(0, 10);

        // When / Then
        assertThatThrownBy(() -> productService.getProductsByCategory(invalidCategory, pageable))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid category");
    }

    @Test
    void shouldGetAllProductsSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("price").ascending());
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        ProductDto expectedDto = TestSuiteUtils.createProductDtoFromProduct(products.get(0));

        given(productRepository.findAll(any(Pageable.class))).willReturn(productPage);
        given(productMapper.toDto(any(Product.class))).willReturn(expectedDto);

        // When
        Page<ProductDto> actual = productService.getAllProducts(0, 2, "price", "asc");

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSize(products.size());
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    // === Update ==================================================================

    @Test
    void shouldUpdateProductSuccessfully() {
        String newTitle = "Updated";
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .title(newTitle)
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .build();
        product.setTitle(newTitle);
        ProductDto expectedDto = TestSuiteUtils.createProductDtoFromProduct(product);

        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(productRepository.save(product)).willReturn(product);
        given(productMapper.toDto(product)).willReturn(expectedDto);

        ProductDto actual = productService.update(product.getId(), updateDto);

        assertThat(actual).isNotNull();
        assertThat(actual.title()).isEqualTo(newTitle);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowWhenProductNotFoundOnUpdate() {
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(product.getId(), any(ProductUpdateDto.class)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ====== Delete ==================================================================

    @Test
    void shouldDeleteProductSuccessfully() {
        // Given
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // When
        productService.deleteProductById(product.getId());

        // Then
        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void shouldThrowWhenProductNotFoundOnDelete() {
        // Given
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.deleteProductById(product.getId()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).delete(any(Product.class));
    }
}