package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.ProvisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/provisions")
public class ProvisionController {

    private final ProvisionService provisionService;

    public ProvisionController(ProvisionService provisionService) {
        this.provisionService = provisionService;
    }

    @PostMapping
    public ResponseEntity<ProvisionResponse> create(@Valid @RequestBody ProvisionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(provisionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvisionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(provisionService.getById(id));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ProvisionResponse> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(provisionService.approve(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ProvisionResponse> close(@PathVariable UUID id) {
        return ResponseEntity.ok(provisionService.close(id));
    }
}