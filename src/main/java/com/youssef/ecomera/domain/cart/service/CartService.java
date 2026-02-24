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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public CartDto getMyCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toDto(cart);
    }

    public CartDto getCartByUserId(UUID userId){
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        if(cart.isEmpty()) {
            throw new ResourceNotFoundException("Cart not found for user: " + userId);
        }
        return cartMapper.toDto(cart.get());
    }

    public CartDto getCartById(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
        return cartMapper.toDto(cart);
    }

    public CartDto addToCart(UUID userId, CartCreateDto request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check stock availability
        if (product.getStock() < request.quantity()) {
            throw new BusinessException("Not enough stock available");
        }

        // Check if product already in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();

            if (newQuantity > product.getStock()) {
                throw new BusinessException("Not enough stock available");
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        log.info("Added product {} to cart for user {}", product.getId(), userId);
        return cartMapper.toDto(savedCart);
    }

    public CartDto updateCartItem(UUID userId, UUID cartItemId, CartItemUpdateDto request) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Check stock
        if (cartItem.getProduct().getStock() < request.quantity()) {
            throw new BusinessException("Not enough stock available");
        }

        cartItem.setQuantity(request.quantity());
        Cart savedCart = cartRepository.save(cart);
        log.info("Updated cart item {} for user {}", cartItemId, userId);
        return cartMapper.toDto(savedCart);
    }

    public CartDto removeCartItem(UUID userId, UUID cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.removeItem(cartItem);
        Cart savedCart = cartRepository.save(cart);
        log.info("Removed cart item {} for user {}", cartItemId, userId);
        return cartMapper.toDto(savedCart);
    }

    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clearItems();
        cartRepository.save(cart);
        log.info("Cleared cart for user {}", userId);
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> {
                    User user = User.builder()
                            .id(userId)
                            .build();
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    // Scheduled task to clean expired carts
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void cleanExpiredCarts() {
        cartRepository.deleteExpiredCarts(LocalDateTime.now());
        log.info("Cleaned expired carts");
    }
}
