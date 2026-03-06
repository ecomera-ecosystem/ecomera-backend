package com.youssef.ecomera.domain.cart.service;

import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.cart.dto.cart.CartCreateDto;
import com.youssef.ecomera.domain.cart.dto.cart.CartDto;
import com.youssef.ecomera.domain.cart.dto.cartitem.CartItemUpdateDto;
import com.youssef.ecomera.domain.cart.entity.Cart;
import com.youssef.ecomera.domain.cart.entity.CartItem;
import com.youssef.ecomera.domain.cart.mapper.CartMapper;
import com.youssef.ecomera.domain.cart.repository.CartItemRepository;
import com.youssef.ecomera.domain.cart.repository.CartRepository;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.repository.ProductRepository;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.utils.TestSuiteUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;
    @Mock
    CartItemRepository cartItemRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    CartMapper cartMapper;

    @InjectMocks
    CartService cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        user = TestSuiteUtils.createUser();
        product = TestSuiteUtils.createProduct();
        cart = TestSuiteUtils.createCart(user);
        cartDto = TestSuiteUtils.createCartDto(cart);
    }

    // ─── getMyCart ───────────────────────────────────────────────────────────────

    @Test
    void shouldGetMyCartSuccessfully() {
        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(cartMapper.toDto(cart)).willReturn(cartDto);

        CartDto actual = cartService.getMyCart(user.getId());

        assertThat(actual).isNotNull();
        assertThat(actual.userId()).isEqualTo(user.getId());
        verify(cartRepository, times(1)).findByUserIdWithItems(user.getId());
    }

    @Test
    void shouldCreateNewCartWhenNotFound() {
        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willReturn(cart);
        given(cartMapper.toDto(cart)).willReturn(cartDto);

        CartDto actual = cartService.getMyCart(user.getId());

        assertThat(actual).isNotNull();
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    // ─── addToCart ───────────────────────────────────────────────────────────────

    @Test
    void shouldAddNewItemToCartSuccessfully() {
        CartCreateDto createDto = TestSuiteUtils.createCartCreateDto(product);

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .willReturn(Optional.empty());
        given(cartRepository.save(cart)).willReturn(cart);
        given(cartMapper.toDto(cart)).willReturn(cartDto);

        CartDto actual = cartService.addToCart(user.getId(), createDto);

        assertThat(actual).isNotNull();
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void shouldMergeQuantityWhenItemAlreadyInCart() {
        CartCreateDto createDto = TestSuiteUtils.createCartCreateDto(product);
        CartItem existingItem = CartItem.builder()
                .id(TestSuiteUtils.TEST_CART_ITEM_ID)
                .cart(cart)
                .product(product)
                .quantity(2)
                .unitPrice(product.getPrice())
                .build();

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .willReturn(Optional.of(existingItem));
        given(cartRepository.save(cart)).willReturn(cart);
        given(cartMapper.toDto(cart)).willReturn(cartDto);

        cartService.addToCart(user.getId(), createDto);

        assertThat(existingItem.getQuantity()).isEqualTo(4); // 2 existing + 2 new
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void shouldThrowWhenProductNotFoundOnAddToCart() {
        CartCreateDto createDto = TestSuiteUtils.createCartCreateDto(product);

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(user.getId(), createDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void shouldThrowWhenInsufficientStockOnAddToCart() {
        product.setStock(1); // less than requested quantity of 2
        CartCreateDto createDto = TestSuiteUtils.createCartCreateDto(product);

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addToCart(user.getId(), createDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("stock");

        verify(cartRepository, never()).save(any(Cart.class));
    }

    // ─── updateCartItem ──────────────────────────────────────────────────────────

    @Test
    void shouldUpdateCartItemSuccessfully() {
        Cart cartWithItems = TestSuiteUtils.createCartWithItems(user, product);
        CartItemUpdateDto updateDto = TestSuiteUtils.createCartItemUpdateDto();
        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cartWithItems));
        given(cartRepository.save(cartWithItems)).willReturn(cartWithItems);
        given(cartMapper.toDto(cartWithItems)).willReturn(cartDto);

        CartDto actual = cartService.updateCartItem(user.getId(), TestSuiteUtils.TEST_CART_ITEM_ID, updateDto);

        assertThat(actual).isNotNull();
        verify(cartRepository, times(1)).save(cartWithItems);
    }

    @Test
    void shouldThrowWhenInsufficientStockOnUpdate() {
        product.setStock(1);
        Cart cartWithItems = TestSuiteUtils.createCartWithItems(user, product);
        CartItemUpdateDto updateDto = CartItemUpdateDto.builder().quantity(10).build();

        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cartWithItems));

        assertThatThrownBy(() -> cartService.updateCartItem(user.getId(), TestSuiteUtils.TEST_CART_ITEM_ID, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("stock");
    }

    // ─── removeCartItem ──────────────────────────────────────────────────────────

    @Test
    void shouldRemoveCartItemSuccessfully() {
        Cart cartWithItems = TestSuiteUtils.createCartWithItems(user, product);
        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cartWithItems));
        given(cartRepository.save(cartWithItems)).willReturn(cartWithItems);
        given(cartMapper.toDto(cartWithItems)).willReturn(cartDto);

        CartDto actual = cartService.removeCartItem(user.getId(), TestSuiteUtils.TEST_CART_ITEM_ID);

        assertThat(actual).isNotNull();
        verify(cartRepository, times(1)).save(cartWithItems);
    }

    @Test
    void shouldThrowWhenCartItemNotFoundOnRemove() {
        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.removeCartItem(user.getId(), UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── clearCart ───────────────────────────────────────────────────────────────

    @Test
    void shouldClearCartSuccessfully() {
        given(cartRepository.findByUserIdWithItems(user.getId())).willReturn(Optional.of(cart));

        cartService.clearCart(user.getId());

        verify(cartRepository, times(1)).save(cart);
    }
}