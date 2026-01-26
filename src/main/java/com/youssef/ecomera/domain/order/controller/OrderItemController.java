package com.youssef.ecomera.domain.order.controller;

import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemUpdateDto;
import com.youssef.ecomera.domain.order.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order-items")
@Tag(name = "Order Items", description = "APIs for managing order items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Operation(summary = "Get all order items", description = "Returns a paginated list of all order items.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order items retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<OrderItemDto>> getAll(
            @Parameter(description = "Pagination information") Pageable pageable) {
        Page<OrderItemDto> orderItems = orderItemService.getAll(pageable);
        return ResponseEntity.ok(orderItems);
    }

    @Operation(summary = "Create a new order item", description = "Creates a new order item and returns the created resource.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order item data"),
            @ApiResponse(responseCode = "404", description = "Related order not found")
    })
    @PostMapping
    public ResponseEntity<OrderItemDto> create(
            @Parameter(description = "Order item creation payload") @Valid @RequestBody OrderItemCreateDto orderItemDto)
            throws ChangeSetPersister.NotFoundException {
        OrderItemDto savedOrderItem = orderItemService.create(orderItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderItem);
    }

    @Operation(summary = "Update an order item", description = "Updates an existing order item by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> update(
            @Parameter(description = "Order item UUID") @PathVariable UUID id,
            @Parameter(description = "Order item update payload") @Valid @RequestBody OrderItemUpdateDto orderItemDto)
            throws ChangeSetPersister.NotFoundException {
        if (orderItemService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        OrderItemDto updatedOrderItem = orderItemService.update(id, orderItemDto);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @Operation(summary = "Get order item by ID", description = "Fetches a single order item by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getById(
            @Parameter(description = "Order item UUID") @PathVariable UUID id) {
        if (orderItemService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        OrderItemDto orderItem = orderItemService.getById(id);
        return ResponseEntity.ok(orderItem);
    }

    @Operation(summary = "Delete order item", description = "Deletes an order item by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Order item UUID") @PathVariable UUID id) {
        if (orderItemService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        orderItemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get order items by order ID", description = "Returns a paginated list of order items belonging to a specific order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order items retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/order")
    public ResponseEntity<Page<OrderItemDto>> getByOrderId(
            @Parameter(description = "Order UUID") @RequestParam("orderId") UUID orderId,
            @Parameter(description = "Pagination information") Pageable pageable) {
        Page<OrderItemDto> orderItems = orderItemService.getByOrderId(orderId, pageable);
        return ResponseEntity.ok(orderItems);
    }
}
