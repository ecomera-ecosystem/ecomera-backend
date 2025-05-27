package com.moushtario.ecomera.mvc.repository;

import com.moushtario.ecomera.mvc.domain.entity.Order;
import com.moushtario.ecomera.mvc.domain.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    Page<OrderItem> findOrderItemsByOrder(Order order, Pageable pageable);
}
