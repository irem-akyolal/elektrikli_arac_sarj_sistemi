package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
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
}