package com.moushtario.ecomera.mvc.repository;

import com.moushtario.ecomera.mvc.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
