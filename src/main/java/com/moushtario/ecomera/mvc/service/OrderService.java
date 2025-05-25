package com.moushtario.ecomera.mvc.service;

import com.moushtario.ecomera.mvc.domain.dto.OrderDto;
import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.mapper.OrderMapper;
import com.moushtario.ecomera.mvc.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderDto saveOrder(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
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
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(orderId);
    }


    public boolean isExists(UUID id) {
        return  orderRepository.existsById(id);
    }
}
