package com.youssef.ecomera.domain.order.service;

import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.common.util.SanitizationUtils;
import com.youssef.ecomera.domain.cart.entity.Cart;
import com.youssef.ecomera.domain.cart.entity.CartItem;
import com.youssef.ecomera.domain.cart.repository.CartRepository;
import com.youssef.ecomera.domain.order.dto.order.OrderCreateDto;
import com.youssef.ecomera.domain.order.dto.order.OrderDto;
import com.youssef.ecomera.domain.order.dto.order.OrderUpdateDto;
import com.youssef.ecomera.domain.order.dto.orderitem.OrderItemCreateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.entity.OrderItem;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.domain.order.mapper.OrderMapper;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.product.entity.Product;
import com.youssef.ecomera.domain.product.repository.ProductRepository;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto create(OrderCreateDto dto) {
        // Validate request
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BusinessException("Cannot create order with empty cart");
        }

        // Find user
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId()));

        // Create order entity (status is set to PENDING by mapper)
        Order order = orderMapper.toEntity(dto);
        order.setUser(user);
        order.setOrderItems(new ArrayList<>());  // Initialize list

        // Build OrderItems
        for (OrderItemCreateDto itemDto : dto.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.productId()));

            // Validate stock availability
            if (product.getStock() < itemDto.quantity()) {
                throw new BusinessException(
                        String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                                product.getTitle(), product.getStock(), itemDto.quantity())
                );
            }

            // Build OrderItem
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .order(order)
                    .quantity(itemDto.quantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.getOrderItems().add(item);

            // Reduce stock
            product.setStock(product.getStock() - itemDto.quantity());
        }

        // Calculate total price using the Order's business method
        order.recalculateTotal();

        // Save (cascades to items)
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


    @Transactional
    public OrderDto addItemToOrder(UUID orderId, OrderItemCreateDto itemRequest) {
        Order order = findOrderById(orderId);

        // Validate order can be modified
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Cannot modify order in status: " + order.getStatus());
        }

        Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.productId()));

        // Check stock
        if (product.getStock() < itemRequest.quantity()) {
            throw new BusinessException("Insufficient stock for product: " + product.getTitle());
        }

        // Create new item
        OrderItem newItem = OrderItem.builder()
                .product(product)
                .order(order)
                .quantity(itemRequest.quantity())
                .unitPrice(product.getPrice())
                .build();

        order.getOrderItems().add(newItem);

        // Reduce stock
        product.setStock(product.getStock() - itemRequest.quantity());

        // Recalculate total
        order.recalculateTotal();

        Order savedOrder = orderRepository.save(order);
        log.info("Item added to order {}: product {}, quantity {}", orderId, product.getTitle(), itemRequest.quantity());

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto removeItemFromOrder(UUID orderId, UUID itemId) {
        Order order = findOrderById(orderId);

        // Validate order can be modified
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Cannot modify order in status: " + order.getStatus());
        }

        OrderItem itemToRemove = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemId));

        // Restore stock
        Product product = itemToRemove.getProduct();
        product.setStock(product.getStock() + itemToRemove.getQuantity());

        // Remove item
        order.getOrderItems().remove(itemToRemove);

        // Can't have empty order
        if (order.getOrderItems().isEmpty()) {
            throw new BusinessException("Cannot remove last item from order. Cancel order instead.");
        }

        // Recalculate total
        order.recalculateTotal();

        Order savedOrder = orderRepository.save(order);
        log.info("Item {} removed from order {}", itemId, orderId);

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto updateItemQuantity(UUID orderId, UUID itemId, Integer newQuantity) {
        if (newQuantity < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }

        Order order = findOrderById(orderId);

        // Validate order can be modified
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Cannot modify order in status: " + order.getStatus());
        }

        OrderItem item = order.getOrderItems().stream()
                .filter(oi -> oi.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemId));

        Product product = item.getProduct();
        int oldQuantity = item.getQuantity();
        int quantityDiff = newQuantity - oldQuantity;

        // Check stock if increasing quantity
        if (quantityDiff > 0) {
            if (product.getStock() < quantityDiff) {
                throw new BusinessException("Insufficient stock. Available: " + product.getStock());
            }
            product.setStock(product.getStock() - quantityDiff);
        } else if (quantityDiff < 0) {
            // Restore stock if decreasing quantity
            product.setStock(product.getStock() + Math.abs(quantityDiff));
        }

        item.setQuantity(newQuantity);
        item.setUnitPrice(product.getPrice());

        // Recalculate total
        order.recalculateTotal();

        Order savedOrder = orderRepository.save(order);
        log.info("Order {} item {} quantity updated: {} -> {}", orderId, itemId, oldQuantity, newQuantity);

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto checkout(UUID userId) {
        // 1. Fetch the cart
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        // 2. Guard against empty cart
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BusinessException("Cannot checkout with an empty cart");
        }

        // 3. Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 4. Build the order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        // 5. Convert cart items → order items
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Validate stock at checkout time
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(
                        String.format("Insufficient stock for '%s'. Available: %d, In cart: %d",
                                SanitizationUtils.sanitize(product.getTitle()), product.getStock(), cartItem.getQuantity())
                );
            }

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .order(order)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice()) // snapshot price at purchase time
                    .build();

            order.addItem(orderItem); // recalculates total automatically
            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        // 6. Save order
        Order savedOrder = orderRepository.save(order);

        // 7. Clear the cart
        cart.clearItems();
        cartRepository.save(cart);

        log.info("Checkout completed for user {}. Order id: {}", userId, savedOrder.getId());
        return orderMapper.toDto(savedOrder);
    }



    // Helper method
    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(Order.class.getSimpleName(), "id", orderId));
    }
}
