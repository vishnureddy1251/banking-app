package com.banking.app.controller;

import com.banking.app.model.BillPayment;
import com.banking.app.service.BillPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bills")
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    @PostMapping("/pay")
    public ResponseEntity<BillPayment> payBill(@Valid @RequestBody BillPayment bill) {
        return new ResponseEntity<>(billPaymentService.payBill(bill), HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<BillPayment>> getPayments(
            @PathVariable Long accountId,
            @RequestParam(required = false) String type) {

        if (type != null && !type.isEmpty()) {
            return ResponseEntity.ok(billPaymentService.getPaymentsByType(accountId, type));
        }
        return ResponseEntity.ok(billPaymentService.getPaymentsByAccountId(accountId));
    }

    @GetMapping("/track/{referenceNumber}")
    public ResponseEntity<BillPayment> trackPayment(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(billPaymentService.getPaymentByReference(referenceNumber));
    }
}
