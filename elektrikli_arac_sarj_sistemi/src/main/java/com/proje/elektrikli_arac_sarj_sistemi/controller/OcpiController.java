package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiCdrProcessingService;
import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiLocationSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ocpi")
public class OcpiController {

    private final OcpiLocationSyncService ocpiLocationSyncService;
    private final OcpiCdrProcessingService ocpiCdrProcessingService;

    public OcpiController(OcpiLocationSyncService ocpiLocationSyncService,
                           OcpiCdrProcessingService ocpiCdrProcessingService) {
        this.ocpiLocationSyncService = ocpiLocationSyncService;
        this.ocpiCdrProcessingService = ocpiCdrProcessingService;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PostMapping("/sync/locations")
    public ResponseEntity<Void> syncLocations() {
        ocpiLocationSyncService.syncLocations();
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PostMapping("/cdr/process")
    public ResponseEntity<Void> processCdr(
            @RequestParam String ocpiSessionId,
            @RequestParam Double totalEnergy) {
        ocpiCdrProcessingService.processCdrForSession(ocpiSessionId, totalEnergy);
        return ResponseEntity.ok().build();
    }
}