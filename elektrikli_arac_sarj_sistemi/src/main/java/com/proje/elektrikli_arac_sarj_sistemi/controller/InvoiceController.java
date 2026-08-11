package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.invoice.InvoiceResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.invoice.InvoiceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/by-session/{chargingSessionId}")
    public ResponseEntity<InvoiceResponse> getBySession(@PathVariable UUID chargingSessionId) {
        return ResponseEntity.ok(invoiceService.getByChargingSessionId(chargingSessionId));
    }
}