package com.proje.elektrikli_arac_sarj_sistemi.controller.location;

import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationUpdateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.location.LocationAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/locations")
@RequiredArgsConstructor
public class LocationAdminController {

    private final LocationAdminService locationAdminService;
    private final PageableValidator pageableValidator;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PostMapping
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationAdminService.create(request));
    }

    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(locationAdminService.update(id, request));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<LocationResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(locationAdminService.activate(id));
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        locationAdminService.deactivate(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationAdminService.getById(id));
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
@GetMapping
public ResponseEntity<Page<LocationResponse>> getAllWithFilters(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) Boolean active,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable) {

       pageableValidator.validate(pageable, SortFields.ADMIN_LOCATION);

    return ResponseEntity.ok(
            locationAdminService.getAllWithFilters(
                    name,
                    city,
                    active,
                    pageable
            )
    );
}

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @GetMapping("/all")
    public ResponseEntity<List<LocationResponse>> getAll() {
        return ResponseEntity.ok(locationAdminService.getAll());
    }
}
