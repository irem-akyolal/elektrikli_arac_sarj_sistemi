package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionStartRequest;
import com.proje.elektrikli_arac_sarj_sistemi.service.ChargingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/charging-sessions")
public class ChargingSessionController {

    private final ChargingSessionService chargingSessionService;

    public ChargingSessionController(ChargingSessionService chargingSessionService) {
        this.chargingSessionService = chargingSessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<ChargingSessionResponse> start(@Valid @RequestBody ChargingSessionStartRequest request) {
        ChargingSessionResponse response = chargingSessionService.startSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingSessionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(chargingSessionService.getById(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ChargingSessionResponse> complete(
            @PathVariable UUID id, @RequestParam BigDecimal energyConsumedKwh) {
        return ResponseEntity.ok(chargingSessionService.completeSession(id, energyConsumedKwh));
    }
}
