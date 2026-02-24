package com.youssef.ecomera.common.security;

import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.payment.repository.PaymentRepository;
import com.youssef.ecomera.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Component("appSecurity")
@SuppressWarnings("unused")
@Transactional(readOnly = true)
public class AppSecurityService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public boolean isOrderOwner(Authentication authentication, UUID orderId) {
        User user = (User) authentication.getPrincipal();
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean isPaymentOwner(Authentication authentication, UUID paymentId) {
        User user = (User) authentication.getPrincipal();
        return paymentRepository.findById(paymentId)
                .map(payment -> payment.getOrder().getUser().getId().equals(user.getId()))
                .orElse(false);
    }
}
