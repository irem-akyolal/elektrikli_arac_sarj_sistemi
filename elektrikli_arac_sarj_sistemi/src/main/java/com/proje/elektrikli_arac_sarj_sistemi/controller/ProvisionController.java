package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.provision.ProvisionAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.service.provision.ProvisionService;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/provisions")
public class ProvisionController {

    private final ProvisionService provisionService;
    private final ProvisionAdminService provisionAdminService;
    private final PageableValidator pageableValidator;

    public ProvisionController(ProvisionService provisionService, ProvisionAdminService provisionAdminService, PageableValidator pageableValidator) {
        this.provisionService = provisionService;
        this.provisionAdminService = provisionAdminService;
        this.pageableValidator = pageableValidator;
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
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<Page<ProvisionResponse>> search(
        @RequestParam(required = false) ProvisionStatus status,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime closedAfter,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime closedBefore,

        Pageable pageable) {

    pageableValidator.validate(pageable, SortFields.ADMIN_PROVISION);

      return ResponseEntity.ok(
            provisionAdminService.search(
                    status,
                    closedAfter,
                    closedBefore,
                    pageable
            )
    );
   }
}