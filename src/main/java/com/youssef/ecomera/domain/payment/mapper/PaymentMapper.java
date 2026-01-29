package com.youssef.ecomera.domain.payment.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.domain.payment.entity.Payment;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;

import org.mapstruct.*;

@Mapper(config = BaseMappingConfig.class)
public interface PaymentMapper extends BaseMapper<Payment, PaymentDto> {

    // === ENTITY → DTO ===
    @Override
    @Mapping(source = "order.id", target = "orderId")
    PaymentDto toDto(Payment payment);


    // === DTO → ENTITY (Create) ===
    @Override
    @Mapping(target = "order", ignore = true)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Payment toEntity(PaymentDto dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "paymentStatus", constant = "PENDING") // Default value
    Payment toEntity(PaymentCreateDto dto);

    // === Update logic (PATCH) ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @Mapping(target = "order", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PaymentUpdateDto dto, @MappingTarget Payment payment);
}
