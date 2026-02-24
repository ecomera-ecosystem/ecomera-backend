package com.youssef.ecomera.common.security;

import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component("appSecurity")
@SuppressWarnings("unused")
public class AppSecurityService {

    private final OrderRepository orderRepository;

    public boolean isOrderOwner(Authentication authentication, UUID orderId) {
        User user = (User) authentication.getPrincipal();
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(user.getId()))
                .orElse(false);
    }
}
