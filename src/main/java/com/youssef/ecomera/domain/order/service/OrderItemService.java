package com.youssef.ecomera.domain.order.service;

import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemDto;
import com.youssef.ecomera.domain.order.dto.orderItem.OrderItemUpdateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.order.mapper.OrderItemMapper;
import com.youssef.ecomera.domain.order.repository.OrderItemRepository;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public OrderItemDto createOrderItem(OrderItemCreateDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.getProductId()));

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", dto.getOrderId()));

        // Map simple fields
        OrderItem orderItem = orderItemMapper.toEntity(dto);
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setUnitPrice(product.getPrice());

        OrderItem saved = orderItemRepository.save(orderItem);
        log.info("OrderItem created: {} for Order {}", saved.getId(), order.getId());

        return orderItemMapper.toDto(saved);
    }

    @Transactional
    public OrderItemDto updateOrderItem(UUID id, OrderItemUpdateDto dto) {
        OrderItem existing = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OrderItem.class.getSimpleName(), "id", id));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.getProductId()));

        orderItemMapper.updateEntityFromDto(dto, existing);
        existing.setProduct(product);
        existing.setUnitPrice(product.getPrice());

        OrderItem updated = orderItemRepository.save(existing);
        log.info("OrderItem {} updated for Order {}", updated.getId(), updated.getOrder().getId());

        return orderItemMapper.toDto(updated);
    }

    public OrderItemDto getOrderItemById(UUID id) {
        return orderItemRepository.findById(id)
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(OrderItem.class.getSimpleName(), "id", id));
    }

    public Page<OrderItemDto> getAllOrderItems(Pageable pageable) {
        return orderItemRepository.findAll(pageable)
                .map(orderItemMapper::toDto);
    }

    public Page<OrderItemDto> getOrderItemsByOrderId(UUID orderId, Pageable pageable) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        return orderItemRepository.findOrderItemsByOrder(order, pageable)
                .map(orderItemMapper::toDto);
    }

    @Transactional
    public void deleteOrderItemById(UUID id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OrderItem.class.getSimpleName(), "id", id));

        orderItemRepository.delete(orderItem);
        log.info("OrderItem deleted: {}", id);
    }

    public boolean isExists(UUID id) {
        return orderItemRepository.existsById(id);
    }
}
