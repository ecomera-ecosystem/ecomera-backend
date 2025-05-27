package com.moushtario.ecomera.mvc.service;

import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemCreateDto;
import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemDto;
import com.moushtario.ecomera.mvc.domain.dto.orderItem.OrderItemUpdateDto;
import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.entity.OrderItem;
import com.moushtario.ecomera.mvc.domain.entity.Product;
import com.moushtario.ecomera.mvc.domain.mapper.OrderItemMapper;
import com.moushtario.ecomera.mvc.repository.OrderItemRepository;
import com.moushtario.ecomera.mvc.repository.OrderRepository;
import com.moushtario.ecomera.mvc.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemDto createOrderItem(OrderItemCreateDto dto) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        // Get entity record
        OrderItem orderItem = orderItemMapper.toEntity(dto);

        orderItem.setQuantity(dto.getQuantity());
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setUnitPrice(product.getPrice());

        OrderItem saved = orderItemRepository.save(orderItem);

        return orderItemMapper.toDto(saved);
    }

    public OrderItemDto updateOrderItem(@NotNull UUID id , OrderItemUpdateDto orderItemDto) throws ChangeSetPersister.NotFoundException {
        OrderItem existing = orderItemRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        orderItemMapper.updateEntityFromDto(orderItemDto, existing);
        existing.setProduct(product);
        existing.setUnitPrice(product.getPrice());

        return orderItemMapper.toDto(orderItemRepository.save(existing));
    }

    public OrderItemDto getOrderItemById(UUID id) {
        var orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found"));
        return orderItemMapper.toDto(orderItem);
    }

    public boolean isExists(UUID id) {
        return !orderItemRepository.existsById(id);
    }

    public void deleteOrderItemById(UUID id) {
        if (!orderItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Order item not found");
        }
        orderItemRepository.deleteById(id);
    }

    public Page<OrderItemDto> getOrderItemsByOrderId(UUID orderId, Pageable pageable) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderItemRepository.findOrderItemsByOrder(order, pageable)
                .map(orderItemMapper::toDto);
    }

    public Page<OrderItemDto> getAllOrderItems(Pageable pageable) {
        return orderItemRepository.findAll(pageable)
                .map(orderItemMapper::toDto);
    }

}
