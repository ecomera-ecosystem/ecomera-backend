package com.youssef.ecomera.domain.payment.service;

import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.mapper.PaymentMapper;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentDto create(PaymentCreateDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        Payment payment = paymentMapper.toEntity(dto);
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    public Page<PaymentDto> getAll(int page, int size, String sortBy, String direction){
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(paymentMapper::toDto);
    }

    public PaymentDto getById(UUID id){
        Payment payment = paymentRepository.findById(id).orElse(null);
        return paymentMapper.toDto(payment);
    }

    public PaymentDto update(UUID id, PaymentUpdateDto dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        paymentMapper.updatePaymentFromDto(dto, payment);

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toDto(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id " + id));
        paymentRepository.delete(payment);
    }



    public boolean existsById(UUID id) {
        return paymentRepository.existsById(id);
    }
}
