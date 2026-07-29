package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.EvseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evses")
public class EvseController {

    private final EvseService evseService;

    public EvseController(EvseService evseService) {
        this.evseService = evseService;
    }

    @PostMapping
    public ResponseEntity<EvseResponse> create(@Valid @RequestBody EvseCreateRequest request) {
        EvseResponse response = evseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(evseService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EvseResponse>> getByLocation(@RequestParam UUID locationId) {
        return ResponseEntity.ok(evseService.getByLocationId(locationId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EvseResponse> updateStatus(@PathVariable UUID id, @RequestParam EvseStatus status) {
        return ResponseEntity.ok(evseService.updateStatus(id, status));
    }
}
