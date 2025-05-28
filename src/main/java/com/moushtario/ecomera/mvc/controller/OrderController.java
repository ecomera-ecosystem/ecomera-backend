package com.moushtario.ecomera.mvc.controller;

import com.moushtario.ecomera.mvc.domain.dto.order.OrderCreateDto;
import com.moushtario.ecomera.mvc.domain.dto.order.OrderDto;
import com.moushtario.ecomera.mvc.domain.dto.order.OrderUpdateDto;
import com.moushtario.ecomera.mvc.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderCreateDto orderDto) {
        OrderDto savedOrder = orderService.create(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable UUID id, @RequestBody OrderUpdateDto dto) {
        return ResponseEntity.ok(orderService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> ordersPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<OrderDto>> getOrdersByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> ordersPage = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderDto>> getOrdersByUser(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> ordersPage = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(ordersPage);
    }


}
