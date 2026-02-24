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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order", description = "Creates a new order and returns the created resource.")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid order data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping
    public ResponseEntity<OrderDto> create(
            @Parameter(description = "Order creation payload") @Valid @RequestBody OrderCreateDto orderDto) {
        OrderDto savedOrder = orderService.create(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @Operation(summary = "Update order status", description = "Updates the status of an existing order by ID.")
    @ApiResponse(responseCode = "200", description = "Order updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid update data")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> update(
            @Parameter(description = "Order UUID") @PathVariable UUID id,
            @Parameter(description = "Order update payload") @Valid @RequestBody OrderUpdateDto dto) {
        return ResponseEntity.ok(orderService.updateStatus(id, dto));
    }

    @Operation(summary = "Delete order", description = "Deletes an order by ID.")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get order by ID", description = "Fetches a single order by its UUID.")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @Operation(summary = "Get all orders", description = "Returns a paginated list of all orders.")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAll(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getAll(pageable));
    }

    @Operation(summary = "Get orders by status", description = "Returns orders filtered by status with pagination.")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status provided")
    @GetMapping("/status")
    public ResponseEntity<Page<OrderDto>> getByStatus(
            @Parameter(description = "Order status", example = "PENDING") @RequestParam String status,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getByStatus(status, pageable));
    }

    @Operation(summary = "Get orders by user", description = "Returns orders for a specific user with pagination.")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/user")
    public ResponseEntity<Page<OrderDto>> getByUser(
            @Parameter(description = "User UUID") @RequestParam UUID userId,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getByUserId(userId, pageable));
    }

    @Operation(summary = "Add item to order", description = "Adds a new item to an existing order")
    @ApiResponse(responseCode = "200", description = "Item added successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "400", description = "Cannot modify completed order")
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto> addItem(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderItemCreateDto itemRequest) {
        return ResponseEntity.ok(orderService.addItemToOrder(orderId, itemRequest));
    }

    @Operation(summary = "Remove item from order", description = "Removes an item from an order")
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> removeItem(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(orderService.removeItemFromOrder(orderId, itemId));
    }

    @Operation(summary = "Update item quantity", description = "Updates quantity of an item in an order")
    @PatchMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto> updateItemQuantity(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderService.updateItemQuantity(orderId, itemId, quantity));
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout", description = "Convert current cart into an order")
    @ApiResponse(responseCode = "201", description = "Order created from cart")
    @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock")
    public ResponseEntity<OrderDto> checkout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        OrderDto order = orderService.checkout(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
