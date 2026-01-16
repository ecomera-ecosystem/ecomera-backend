package com.youssef.ecomera.domain.payment.mapper;

import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.order.entity.Order;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.enums.PaymentMethod;
import com.youssef.ecomera.domain.payment.enums.PaymentStatus;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // === ENTITY → DTO ===
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "paymentMethod", target = "paymentMethod", qualifiedByName = "enumToString")
    @Mapping(source = "paymentStatus", target = "paymentStatus", qualifiedByName = "enumToString")
    PaymentDto toDto(Payment payment);

    // === DTO → ENTITY (Create) ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "order", ignore = true) // Set manually in service
    @Mapping(source = "paymentMethod", target = "paymentMethod", qualifiedByName = "stringToPaymentMethod")
    @Mapping(target = "paymentStatus", constant = "PENDING") // Default value
    Payment toEntity(PaymentCreateDto dto);

    // === Update logic ===
    // === Manual PATCH update ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentFromDto(PaymentUpdateDto dto, @MappingTarget Payment payment);


    // === Custom Converters ===
    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e != null ? e.name().toUpperCase() : null;
    }

    @Named("stringToPaymentMethod")
    default PaymentMethod stringToPaymentMethod(String method) {
        return method != null ? PaymentMethod.valueOf(method.toUpperCase()) : null;
    }

    @Named("stringToPaymentStatus")
    default PaymentStatus stringToPaymentStatus(String status) {
        return status != null ? PaymentStatus.valueOf(status.toUpperCase()) : null;
    }

    // === Helper: from UUID to Order ===
    default Order mapOrderId(UUID orderId) {
        Order order = new Order();
        order.setId(orderId);
        return order;
    }
}

