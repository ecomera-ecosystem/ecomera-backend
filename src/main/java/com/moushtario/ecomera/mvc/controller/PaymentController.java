package com.moushtario.ecomera.mvc.controller;

import com.moushtario.ecomera.mvc.domain.dto.payment.PaymentCreateDto;
import com.moushtario.ecomera.mvc.domain.dto.payment.PaymentDto;
import com.moushtario.ecomera.mvc.domain.dto.payment.PaymentUpdateDto;
import com.moushtario.ecomera.mvc.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // ==== READ ALL ====
    @GetMapping
    public Page<PaymentDto> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ){
        return paymentService.getAll(page, size, sortBy, direction);
    }

    // ==== READ BY ID ====
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable UUID id) {
        if (!paymentService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        PaymentDto payment = paymentService.getById(id);
        return ResponseEntity.ok(payment);
    }


    // ==== CREATE ====
    @PostMapping
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentCreateDto paymentCreateDto){
        PaymentDto paymentDto = paymentService.create(paymentCreateDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentDto);
    }
    // ==== UPDATE ====
    @PatchMapping("/{id}")
    public ResponseEntity<PaymentDto> update(@PathVariable UUID id, @RequestBody PaymentUpdateDto dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }
    // ==== DELETE ====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!paymentService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
