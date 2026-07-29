package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.ConnectorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/connectors")
public class ConnectorController {

    private final ConnectorService connectorService;

    public ConnectorController(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    @PostMapping
    public ResponseEntity<ConnectorResponse> create(@Valid @RequestBody ConnectorCreateRequest request) {
        ConnectorResponse response = connectorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConnectorResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(connectorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ConnectorResponse>> getByEvse(@RequestParam UUID evseId) {
        return ResponseEntity.ok(connectorService.getByEvseId(evseId));
    }

    @PatchMapping("/{id}/unit-price")
    public ResponseEntity<ConnectorResponse> updateUnitPrice(
            @PathVariable UUID id, @RequestParam BigDecimal price) {
        return ResponseEntity.ok(connectorService.updateUnitPrice(id, price));
    }
}
