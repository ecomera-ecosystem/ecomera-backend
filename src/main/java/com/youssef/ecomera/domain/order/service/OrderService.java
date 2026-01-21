package com.youssef.ecomera.domain.order.service;

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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderDto create(OrderCreateDto dto) {
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Order order = orderMapper.toEntity(dto);
        order.setUser(user);

        // Build OrderItems and calculate total
        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            OrderItem item = orderItemMapper.toEntity(itemDto);
            item.setProduct(product);
            item.setOrder(order); // set back-reference
            item.setUnitPrice(product.getPrice());

            return item;
        }).toList();

        order.setOrderItems(items);

        BigDecimal total = items.stream()
                .map(it -> it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(total);

        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto updateStatus(UUID id, OrderUpdateDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        orderMapper.updateEntityFromDto(dto, order);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    public Page<OrderDto> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(orderMapper.mapStatus(status), pageable)
                .map(orderMapper::toDto);
    }

    public Page<OrderDto> getOrdersByUserId(UUID userId, Pageable pageable) {
        return orderRepository.findByUser_Id(userId, pageable)
                .map(orderMapper::toDto);
    }

    public void deleteOrderById(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found"); // TODO: Custom Exception in issue #5
        }
        orderRepository.deleteById(orderId);
    }


    public boolean isExists(UUID id) {
        return  orderRepository.existsById(id);
    }
}
