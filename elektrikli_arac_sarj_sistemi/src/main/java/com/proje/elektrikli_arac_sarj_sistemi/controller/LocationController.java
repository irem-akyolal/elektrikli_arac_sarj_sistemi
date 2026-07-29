package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationCreateRequest request) {
        LocationResponse response = locationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<LocationResponse>> getAllActive() {
        return ResponseEntity.ok(locationService.getAllActive()); // public website kullanacak
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        locationService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
