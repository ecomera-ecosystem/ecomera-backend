package com.moushtario.ecomera.mvc.controller;

import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemCreateDto;
import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemDto;
import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemUpdateDto;
import com.moushtario.ecomera.mvc.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    // ==== GET ALL ====
    @GetMapping
    public ResponseEntity<Page<OrderItemDto>> getOrderItems(Pageable pageable) {
        Page<OrderItemDto> orderItems = orderItemService.getAllOrderItems(pageable);
        return ResponseEntity.ok(orderItems);
    }
    // ==== CREATE ====
    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody OrderItemCreateDto orderItemDto) throws ChangeSetPersister.NotFoundException {
        OrderItemDto savedOrderItem = orderItemService.createOrderItem(orderItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderItem);
    }
    // ==== UPDATE ====
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable("id") UUID id, @RequestBody OrderItemUpdateDto orderItemDto) throws ChangeSetPersister.NotFoundException {
        if (orderItemService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }

        OrderItemDto updatedOrderItem = orderItemService.updateOrderItem(id, orderItemDto);
        return ResponseEntity.ok(updatedOrderItem);
    }
    // === GET BY ID ===
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItemById(@PathVariable("id") UUID id) {
        if (orderItemService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        OrderItemDto orderItem = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItem);
    }
    // === DELETE ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable("id") UUID id) {
        if (orderItemService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        orderItemService.deleteOrderItemById(id);
        return ResponseEntity.noContent().build();
    }
    // ==== FILTER BY ORDER ====
    @GetMapping("/order")
    public ResponseEntity<Page<OrderItemDto>> getOrderItemsByOrderId(@RequestParam("orderId") UUID orderId, Pageable pageable) {
        Page<OrderItemDto> orderItems = orderItemService.getOrderItemsByOrderId(orderId, pageable);
        return ResponseEntity.ok(orderItems);
    }
}
