package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionStartRequest;
import com.proje.elektrikli_arac_sarj_sistemi.service.session.ChargingSessionService;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.service.session.ChargingSessionAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;


import java.time.LocalDateTime;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/charging-sessions")
public class ChargingSessionController {

    private final ChargingSessionService chargingSessionService;
    private final ChargingSessionAdminService chargingSessionAdminService;
    private final PageableValidator pageableValidator;


    public ChargingSessionController(ChargingSessionService chargingSessionService,ChargingSessionAdminService chargingSessionAdminService, PageableValidator pageableValidator) {
        this.chargingSessionService = chargingSessionService;
        this.chargingSessionAdminService = chargingSessionAdminService;
        this.pageableValidator = pageableValidator;
    }

    @PostMapping("/start")
    public ResponseEntity<ChargingSessionResponse> start(@Valid @RequestBody ChargingSessionStartRequest request) {
        ChargingSessionResponse response = chargingSessionService.startSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/charging")
public ResponseEntity<ChargingSessionResponse> markAsCharging(@PathVariable UUID id) {
    return ResponseEntity.ok(chargingSessionService.markAsCharging(id));
}

@PatchMapping("/{id}/connector-removed")
public ResponseEntity<ChargingSessionResponse> markConnectorRemoved(@PathVariable UUID id) {
    return ResponseEntity.ok(chargingSessionService.markConnectorRemoved(id));
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

     @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
     @GetMapping("/search")
     public ResponseEntity<Page<ChargingSessionResponse>> search(
        @RequestParam(required = false) SessionStatus status,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String plateNumber,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startedAfter,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startedBefore,

        Pageable pageable) {

    pageableValidator.validate(pageable, SortFields.ADMIN_CHARGING_SESSION);

     return ResponseEntity.ok(
            chargingSessionAdminService.search(
                    status,
                    email,
                    plateNumber,
                    startedAfter,
                    startedBefore,
                    pageable
            )
    );
   }
}
