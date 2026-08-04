package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiLocationSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ocpi")
public class OcpiController {

    private final OcpiLocationSyncService ocpiLocationSyncService;

    public OcpiController(OcpiLocationSyncService ocpiLocationSyncService) {
        this.ocpiLocationSyncService = ocpiLocationSyncService;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PostMapping("/sync/locations")
    public ResponseEntity<Void> syncLocations() {
        ocpiLocationSyncService.syncLocations();
        return ResponseEntity.ok().build();
    }
}