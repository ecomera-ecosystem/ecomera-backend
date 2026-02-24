package com.youssef.ecomera.domain.cart.controller;

import com.youssef.ecomera.domain.cart.dto.cart.CartCreateDto;
import com.youssef.ecomera.domain.cart.dto.cart.CartDto;
import com.youssef.ecomera.domain.cart.dto.cartitem.CartItemUpdateDto;
import com.youssef.ecomera.domain.cart.service.CartService;
import com.youssef.ecomera.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Cart", description = "Shopping cart management APIs")
public class CartController {

    private final CartService cartService;

    // Every authenticated user can see their own cart
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get authenticated user's cart")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    public ResponseEntity<CartDto> getMyCart(@AuthenticationPrincipal User user) {
        CartDto cart = cartService.getMyCart(user.getId());
        return ResponseEntity.ok(cart);
    }

    // Manager and Admin can look up any user's cart
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ')")
    @Operation(summary = "Get cart by user ID")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found for user")
    public ResponseEntity<CartDto> getCartByUserId(@PathVariable UUID userId) {
        CartDto cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    // Manager and Admin can look up any cart by ID
    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ')")
    @Operation(summary = "Get cart by ID")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found")
    public ResponseEntity<CartDto> getCartById(@PathVariable UUID cartId) {
        CartDto cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    // Any authenticated user can add to their own cart
    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Add item to cart")
    @ApiResponse(responseCode = "201", description = "Item added successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "400", description = "Insufficient stock")
    public ResponseEntity<CartDto> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartCreateDto request) {
        User user = (User) userDetails;
        CartDto cart = cartService.addToCart(user.getId(), request);
        return ResponseEntity.ok(cart);
    }

    // Any authenticated user can update their own cart items
    @PatchMapping("/items/{cartItemId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Update cart item")
    @ApiResponse(responseCode = "200", description = "Item updated successfully")
    @ApiResponse(responseCode = "404", description = "Cart item not found")
    @ApiResponse(responseCode = "400", description = "Insufficient stock")
    public ResponseEntity<CartDto> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID cartItemId,
            @Valid @RequestBody CartItemUpdateDto request) {
        User user = (User) userDetails;
        CartDto cart = cartService.updateCartItem(user.getId(), cartItemId, request);
        return ResponseEntity.ok(cart);
    }

    // Any authenticated user can remove from their own cart
    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Remove cart item")
    @ApiResponse(responseCode = "200", description = "Item removed successfully")
    @ApiResponse(responseCode = "404", description = "Cart item not found")
    public ResponseEntity<CartDto> removeCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID cartItemId) {
        User user = (User) userDetails;
        CartDto cart = cartService.removeCartItem(user.getId(), cartItemId);
        return ResponseEntity.ok(cart);
    }

    // Any authenticated user can clear their own cart
    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Clear cart")
    @ApiResponse(responseCode = "204", description = "Cart cleared successfully")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }
}