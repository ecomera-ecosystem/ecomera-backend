package com.youssef.ecomera.domain.payment.mapper;

import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.payment.entity.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // === ENTITY → DTO ===
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    PaymentDto toDto(Payment payment);

    // === DTO → ENTITY (Create) ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "order", ignore = true) // Set manually in service
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(target = "paymentStatus", constant = "PENDING") // Default value
    Payment toEntity(PaymentCreateDto dto);

    // === Update logic (PATCH) ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentFromDto(PaymentUpdateDto dto, @MappingTarget Payment payment);
}
