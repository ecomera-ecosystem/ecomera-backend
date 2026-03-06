package com.youssef.ecomera.domain.order.controller;

import com.youssef.ecomera.domain.order.dto.order.OrderCreateDto;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.service.OrderService;
import com.youssef.ecomera.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    // USER creates order manually (checkout is preferred flow)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid order data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @SuppressWarnings("taint")
    public ResponseEntity<OrderDto> create(
            @Parameter(description = "Order creation payload") @Valid @RequestBody OrderCreateDto orderDto) {
        OrderDto savedOrder = orderService.create(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    // Only MANAGER/ADMIN can update order status
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_UPDATE')")
    @Operation(summary = "Update order status")
    @ApiResponse(responseCode = "200", description = "Order updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<OrderDto> update(
            @Parameter(description = "Order UUID") @PathVariable UUID id,
            @Parameter(description = "Order update payload") @Valid @RequestBody OrderUpdateDto dto) {
        return ResponseEntity.ok(orderService.updateStatus(id, dto));
    }

    // Only ADMIN can hard delete orders
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete order")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // USER can fetch their own order, MANAGER/ADMIN fetch any
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ') or @appSecurity.isOrderOwner(authentication, #id)")
    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<OrderDto> getById(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    // MANAGER/ADMIN only - full order list
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ')")
    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<Page<OrderDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getAll(pageable));
    }

    // MANAGER/ADMIN only - filter by status
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ')")
    @Operation(summary = "Get orders by status")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status provided")
    public ResponseEntity<Page<OrderDto>> getByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getByStatus(status, pageable));
    }

    // USER can fetch own orders, MANAGER/ADMIN fetch any user's orders
    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ') or #userId == authentication.principal.id")
    @Operation(summary = "Get orders by user")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Page<OrderDto>> getByUser(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getByUserId(userId, pageable));
    }

    // MANAGER/ADMIN can manually add items to an order
    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_UPDATE')")
    @Operation(summary = "Add item to order")
    @ApiResponse(responseCode = "200", description = "Item added successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "400", description = "Cannot modify completed order")
    public ResponseEntity<OrderDto> addItem(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderItemCreateDto itemRequest) {
        return ResponseEntity.ok(orderService.addItemToOrder(orderId, itemRequest));
    }

    // MANAGER/ADMIN can manually remove items from an order
    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_UPDATE')")
    @Operation(summary = "Remove item from order")
    public ResponseEntity<OrderDto> removeItem(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(orderService.removeItemFromOrder(orderId, itemId));
    }

    // MANAGER/ADMIN can manually update item quantities
    @PatchMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_UPDATE')")
    @Operation(summary = "Update item quantity", description = "Updates quantity of an item in an order")
    public ResponseEntity<OrderDto> updateItemQuantity(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderService.updateItemQuantity(orderId, itemId, quantity));
    }

    // Any authenticated user can checkout their own cart
    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Checkout", description = "Convert current cart into an order")
    @ApiResponse(responseCode = "201", description = "Order created from cart")
    @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock")
    public ResponseEntity<OrderDto> checkout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        OrderDto order = orderService.checkout(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}