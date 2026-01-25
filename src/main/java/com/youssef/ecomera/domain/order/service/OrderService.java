package com.youssef.ecomera.domain.order.service;

import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.order.dto.order.OrderCreateDto;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.order.mapper.OrderItemMapper;
import com.youssef.ecomera.domain.order.mapper.OrderMapper;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.product.repository.ProductRepository;
import com.youssef.ecomera.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.youssef.ecomera.common.exception.BusinessException;

import com.youssef.ecomera.user.entity.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public OrderDto create(OrderCreateDto dto) {
        // Validate request
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BusinessException("Cannot create order with empty cart");
        }

        // Find user
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId()));

        // Map DTO to entity
        Order order = orderMapper.toEntity(dto);
        order.setUser(user);

        // Build OrderItems and validate stock
        List<OrderItem> items = dto.items().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.productId()));

            // Validate stock availability
            if (product.getStock() < itemDto.quantity()) {
                throw new BusinessException(
                        String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                                product.getTitle(), product.getStock(), itemDto.quantity())
                );
            }

            // Map to OrderItem
            OrderItem item = orderItemMapper.toEntity(itemDto);
            item.setProduct(product);
            item.setOrder(order);
            item.setUnitPrice(product.getPrice());

            return item;
        }).toList();

        order.setOrderItems(items);

        // Calculate total price
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);

        // Save and return
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {} for user: {}", savedOrder.getId(), user.getEmail());

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto updateStatus(UUID id, OrderUpdateDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", id));

        // Validate status transition (optional business logic)
        // e.g., can't change from DELIVERED to PENDING

        orderMapper.updateEntityFromDto(dto, order);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order {} status updated to: {}", id, updatedOrder.getStatus());
        return orderMapper.toDto(updatedOrder);
    }

    public OrderDto getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", orderId));
    }

    public Page<OrderDto> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    public Page<OrderDto> getByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(orderMapper.mapStatus(status), pageable)
                .map(orderMapper::toDto);
    }

    public Page<OrderDto> getByUserId(UUID userId, Pageable pageable) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return orderRepository.findByUser_Id(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Transactional
    public void deleteById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(getClass().getSimpleName(), "id", orderId));

        // Optional: Add business logic validation
        // e.g., can't delete DELIVERED orders

        orderRepository.delete(order);
        log.info("Order deleted: {}", orderId);
    }

    public boolean existsById(UUID id) {
        return orderRepository.existsById(id);
    }
}
