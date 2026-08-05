package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.evse.EvseAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.service.evse.EvseService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evses")
public class EvseController {

    private final EvseService evseService;
    private final EvseAdminService evseAdminService;

    public EvseController(EvseService evseService, EvseAdminService evseAdminService) {
        this.evseService = evseService;
        this.evseAdminService = evseAdminService;
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
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

     @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<EvseResponse> updateStatus(@PathVariable UUID id, @RequestParam EvseStatus status) {
        return ResponseEntity.ok(evseService.updateStatus(id, status));
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<Page<EvseResponse>> search(
        @RequestParam(required = false) EvseStatus status,
        @RequestParam(required = false) UUID locationId,
        Pageable pageable) {
            return ResponseEntity.ok(evseAdminService.search(status, locationId, pageable));   
}

}
