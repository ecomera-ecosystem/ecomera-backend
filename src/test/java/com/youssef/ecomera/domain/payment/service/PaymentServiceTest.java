package com.youssef.ecomera.domain.payment.service;

import com.youssef.ecomera.common.exception.AlreadyExistException;
import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.order.enums.OrderStatus;
import com.youssef.ecomera.domain.order.repository.OrderRepository;
import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.enums.PaymentMethod;
import com.youssef.ecomera.domain.payment.enums.PaymentStatus;
import com.youssef.ecomera.domain.payment.mapper.PaymentMapper;
import com.youssef.ecomera.domain.payment.repository.PaymentRepository;
import com.youssef.ecomera.utils.TestSuiteUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    PaymentMapper paymentMapper;
    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    PaymentService paymentService;

    private Order order;
    private Payment payment;
    private PaymentDto paymentDto;

    @BeforeEach
    void setUp() {
        order = TestSuiteUtils.createMinimalOrder();
        payment = TestSuiteUtils.createPayment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalPrice());
        paymentDto = PaymentDto.builder()
                .id(payment.getId())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    // ─── create ──────────────────────────────────────────────────────────────────

    @Test
    void shouldCreatePaymentSuccessfully() {
        PaymentCreateDto createDto = new PaymentCreateDto(PaymentMethod.PAYPAL, order.getId(), "TXN-123");

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(paymentMapper.toEntity(any(PaymentCreateDto.class))).willReturn(payment);
        given(paymentRepository.save(payment)).willReturn(payment);
        given(paymentMapper.toDto(payment)).willReturn(paymentDto);

        PaymentDto result = paymentService.create(createDto);

        assertThat(result).isNotNull();
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void shouldThrowWhenOrderAlreadyHasPayment() {
        order.setPayment(payment);
        PaymentCreateDto createDto = new PaymentCreateDto(PaymentMethod.PAYPAL, order.getId(), "TXN-123");

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.create(createDto))
                .isInstanceOf(AlreadyExistException.class);
    }

    @Test
    void shouldThrowWhenOrderIsCanceledOnCreate() {
        order.setStatus(OrderStatus.CANCELED);
        PaymentCreateDto createDto = new PaymentCreateDto(PaymentMethod.PAYPAL, order.getId(), "TXN-123");

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.create(createDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("canceled order");
    }

    @Test
    void shouldThrowWhenOrderNotFoundOnCreate() {
        PaymentCreateDto createDto = new PaymentCreateDto(PaymentMethod.PAYPAL, UUID.randomUUID(), "TXN-123");

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.create(createDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── update ──────────────────────────────────────────────────────────────────

    @Test
    void shouldUpdatePaymentStatusSuccessfully() {
        PaymentUpdateDto updateDto = PaymentUpdateDto.builder()
                .paymentStatus(PaymentStatus.COMPLETED)
                        .build();

        given(paymentRepository.findById(payment.getId())).willReturn(Optional.of(payment));
        given(paymentRepository.save(payment)).willReturn(payment);
        given(paymentMapper.toDto(payment)).willReturn(paymentDto);

        PaymentDto result = paymentService.update(payment.getId(), updateDto);

        assertThat(result).isNotNull();
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void shouldThrowWhenCompletedPaymentChangedToNonRefunded() {
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        PaymentUpdateDto updateDto = PaymentUpdateDto.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.findById(payment.getId())).willReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.update(payment.getId(), updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("refunded");
    }

    @Test
    void shouldThrowWhenRefundedPaymentStatusChanged() {
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        PaymentUpdateDto updateDto = PaymentUpdateDto.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        given(paymentRepository.findById(payment.getId())).willReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.update(payment.getId(), updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("refunded payment");
    }

    @Test
    void shouldThrowWhenFailedPaymentStatusChanged() {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        PaymentUpdateDto updateDto = PaymentUpdateDto.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        given(paymentRepository.findById(payment.getId())).willReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.update(payment.getId(), updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("failed payment");
    }

    // ─── delete ──────────────────────────────────────────────────────────────────

    @Test
    void shouldDeletePaymentSuccessfully() {
        payment.setPaymentStatus(PaymentStatus.PENDING);

        given(paymentRepository.findById(payment.getId())).willReturn(Optional.of(payment));

        paymentService.delete(payment.getId());

        verify(paymentRepository, times(1)).delete(payment);
    }

    @Test
    void shouldThrowWhenDeletingCompletedPayment() {
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        given(paymentRepository.findById(payment.getId())).willReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.delete(payment.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete completed payment");

        verify(paymentRepository, never()).delete(any(Payment.class));
    }

    @Test
    void shouldThrowWhenPaymentNotFoundOnDelete() {
        given(paymentRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.delete(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}