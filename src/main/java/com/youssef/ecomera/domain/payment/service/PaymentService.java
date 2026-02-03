package com.youssef.ecomera.domain.payment.service;

import com.youssef.ecomera.common.exception.AlreadyExistException;
import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.enums.PaymentStatus;
import com.youssef.ecomera.domain.payment.mapper.PaymentMapper;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentDto create(PaymentCreateDto dto) {
        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", dto.orderId()));
        log.info("Creating payment for order {} using method {}", order.getId(), dto);

        // Check if order already has payment
        if (order.getPayment() != null) {
            throw new AlreadyExistException(Payment.class.getSimpleName(), "orderId", dto.orderId());
        }

        // Validate order is in valid state for payment
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Cannot create payment for canceled order");
        }

        // Create payment
        Payment payment = paymentMapper.toEntity(dto);
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());  // ✅ Amount from order
        payment.setPaymentStatus(PaymentStatus.PENDING);  // ✅ Initial status

        // Sync bidirectional relationship
        order.setPayment(payment);

        Payment saved = paymentRepository.save(payment);

        log.info("Payment created for order {}: {} - {}",
                order.getId(), saved.getId(), saved.getPaymentMethod());

        return paymentMapper.toDto(saved);
    }

    public Page<PaymentDto> getAll(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    }

    public PaymentDto getById(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(Payment.class.getSimpleName(), "id", id));
    }

    @Transactional
    public PaymentDto update(UUID id, PaymentUpdateDto dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Payment.class.getSimpleName(), "id", id));

        // Validate status transitions
        PaymentStatus currentStatus = payment.getPaymentStatus();
        PaymentStatus newStatus = dto.paymentStatus();

        if (newStatus != null) {
            validateStatusTransition(currentStatus, newStatus);
        }

        paymentMapper.updateEntityFromDto(dto, payment);
        Payment updated = paymentRepository.save(payment);

        // Update order status based on payment status
        if (updated.getPaymentStatus() == PaymentStatus.COMPLETED) {
            updated.getOrder().setStatus(OrderStatus.PROCESSING);
        } else if (updated.getPaymentStatus() == PaymentStatus.FAILED) {
            updated.getOrder().setStatus(OrderStatus.CANCELED);
        } else if (updated.getPaymentStatus() == PaymentStatus.REFUNDED) {
            updated.getOrder().setStatus(OrderStatus.CANCELED);
        }

        log.info("Payment {} status updated: {} -> {}", id, currentStatus, updated.getPaymentStatus());
        return paymentMapper.toDto(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Payment.class.getSimpleName(), "id", id));

        // Business validation: can only delete failed/pending payments
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessException("Cannot delete completed payment. Use refund instead.");
        }

        // Clear bidirectional relationship
        Order order = payment.getOrder();
        order.setPayment(null);

        paymentRepository.delete(payment);
        log.info("Payment {} deleted", id);
    }

    private void validateStatusTransition(PaymentStatus current, PaymentStatus newStatus) {
        // COMPLETED can only go to REFUNDED
        if (current == PaymentStatus.COMPLETED && newStatus != PaymentStatus.REFUNDED) {
            throw new BusinessException("Completed payment can only be refunded");
        }

        // REFUNDED is final
        if (current == PaymentStatus.REFUNDED) {
            throw new BusinessException("Cannot change status of refunded payment");
        }

        // FAILED is final
        if (current == PaymentStatus.FAILED) {
            throw new BusinessException("Cannot change status of failed payment");
        }
    }
}