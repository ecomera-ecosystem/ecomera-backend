package com.youssef.ecomera.domain.payment.controller;

import com.youssef.ecomera.domain.payment.dto.PaymentCreateDto;
import com.youssef.ecomera.domain.payment.dto.PaymentDto;
import com.youssef.ecomera.domain.payment.dto.PaymentUpdateDto;
import com.youssef.ecomera.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment Management API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Get all payments", description = "Returns a paginated list of all payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    })
    @GetMapping
    public Page<PaymentDto> getAll(
            @Parameter(description = "Page number (default 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (default 10)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (default createdAt)") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc, default desc)") @RequestParam(defaultValue = "desc") String direction
    ) {
        return paymentService.getAll(page, size, sortBy, direction);
    }

    @Operation(summary = "Get payment by ID", description = "Fetches a single payment by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@Parameter(description = "Payment UUID") @PathVariable UUID id) {
        PaymentDto payment = paymentService.getById(id);
        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "Create a new payment", description = "Creates a new payment and returns the created resource.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data")
    })
    @PostMapping
    public ResponseEntity<PaymentDto> create(@Valid @Parameter(description = "Payment creation payload") @RequestBody PaymentCreateDto dto) {
        PaymentDto saved = paymentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Update a payment", description = "Updates an existing payment by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PaymentDto> update(
            @Parameter(description = "Payment UUID") @PathVariable UUID id,
            @Parameter(description = "Payment update payload") @Valid @RequestBody PaymentUpdateDto dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    @Operation(summary = "Delete a payment", description = "Deletes a payment by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Payment UUID") @PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
