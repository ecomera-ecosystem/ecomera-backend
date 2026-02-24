package com.youssef.ecomera.domain.payment.controller;

import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Payments", description = "Payment Management API")
public class PaymentController {

    private final PaymentService paymentService;

    // MANAGER/ADMIN only — full payment list
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ')")
    @Operation(summary = "Get all payments")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public Page<PaymentDto> getAll(
            @Parameter(description = "Page number (default 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default 10)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (default createdAt)") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc, default desc)") @RequestParam(defaultValue = "desc") String direction
    ) {
        return paymentService.getAll(page, size, sortBy, direction);
    }

    // USER can fetch their own payment, MANAGER/ADMIN fetch any
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_READ') or @appSecurity.isPaymentOwner(authentication, #id)")
    @Operation(summary = "Get payment by ID")
    @ApiResponse(responseCode = "200", description = "Payment retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    // Any authenticated user can create a payment (tied to their order)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Create a new payment")
    @ApiResponse(responseCode = "201", description = "Payment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid payment data")
    public ResponseEntity<PaymentDto> create(@Valid @RequestBody @Parameter(description = "Payment creation payload") PaymentCreateDto dto) {
        PaymentDto saved = paymentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // MANAGER/ADMIN can update payment status
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGER_UPDATE')")
    @Operation(summary = "Update a payment")
    @ApiResponse(responseCode = "200", description = "Payment updated successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentDto> update(
            @Parameter(description = "Payment UUID") @PathVariable UUID id,
            @Parameter(description = "Payment update payload") @Valid @RequestBody PaymentUpdateDto dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    // ADMIN only - hard delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a payment")
    @ApiResponse(responseCode = "204", description = "Payment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Void> delete(@Parameter(description = "Payment UUID") @PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}