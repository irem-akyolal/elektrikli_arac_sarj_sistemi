package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.connector.ConnectorAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.service.connector.ConnectorService;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/connectors")
public class ConnectorController {

    private final ConnectorService connectorService;
    private final ConnectorAdminService connectorAdminService;
    private final PageableValidator pageableValidator;

    public ConnectorController(ConnectorService connectorService, ConnectorAdminService connectorAdminService, PageableValidator pageableValidator) {
        this.connectorService = connectorService;
        this.connectorAdminService = connectorAdminService;
        this.pageableValidator = pageableValidator;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
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


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PatchMapping("/{id}/unit-price")
    public ResponseEntity<ConnectorResponse> updateUnitPrice(
            @PathVariable UUID id, @RequestParam BigDecimal price) {
        return ResponseEntity.ok(connectorService.updateUnitPrice(id, price));
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<Page<ConnectorResponse>> search(
        @RequestParam(required = false) ConnectorStandard standard,
        @RequestParam(required = false) PowerType powerType,
        @RequestParam(required = false) UUID evseId,
        Pageable pageable) {

    pageableValidator.validate(pageable, SortFields.ADMIN_CONNECTOR);

      return ResponseEntity.ok(
            connectorAdminService.search(
                    standard,
                    powerType,
                    evseId,
                    pageable
            )
    );
   }

}
