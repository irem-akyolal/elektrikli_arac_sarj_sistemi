package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.payment.PaymentAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.service.payment.PaymentService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentAdminService paymentAdminService;

    public PaymentController(PaymentService paymentService, PaymentAdminService paymentAdminService) {
        this.paymentService = paymentService;
        this.paymentAdminService = paymentAdminService;
    }

    // Sadece admin panel için — otomatik süreç başarısız olduysa manuel tetikleme
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PostMapping("/capture")
    public ResponseEntity<PaymentResponse> captureManually(@RequestParam UUID provisionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.captureForProvision(provisionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @GetMapping("/search")
      public ResponseEntity<Page<PaymentResponse>> search(
        @RequestParam(required = false) PaymentStatus status,
        @RequestParam(required = false) String transactionId,
        Pageable pageable) {
    return ResponseEntity.ok(paymentAdminService.search(status, transactionId, pageable));
}
}