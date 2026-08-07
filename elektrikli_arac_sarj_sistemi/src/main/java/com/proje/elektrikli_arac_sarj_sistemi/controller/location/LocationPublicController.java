package com.proje.elektrikli_arac_sarj_sistemi.controller.location;

import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationDetailResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.location.LocationPublicService;
import com.proje.elektrikli_arac_sarj_sistemi.util.LocationSearchValidator;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/locations")
@RequiredArgsConstructor
public class LocationPublicController {

    private final LocationPublicService locationPublicService;
    private final PageableValidator pageableValidator;
    private final LocationSearchValidator locationSearchValidator;

    //  Tüm aktif lokasyonlar (filtresiz)
    @GetMapping("/active")
    public ResponseEntity<List<LocationResponse>> getActiveLocations() {
        return ResponseEntity.ok(locationPublicService.getActiveLocations());
    }

    //  Lokasyon detayı
    @GetMapping("/{id}")
    public ResponseEntity<LocationDetailResponse> getLocationDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(locationPublicService.getLocationDetail(id));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<LocationResponse>> getNearby(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam(defaultValue = "10") double radiusKm) {
          return ResponseEntity.ok(locationPublicService.getNearbyLocations(latitude, longitude, radiusKm));
}

    //  Filtreli arama (isim, şehir, konnektör tipi, müsaitlik)
    @GetMapping("/search")
    public ResponseEntity<Page<LocationResponse>> searchActiveLocations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String connectorType,
            @RequestParam(required = false) Boolean onlyAvailable,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

            locationSearchValidator.validate(
            name,
            city,
            connectorType
        );

            pageableValidator.validate(pageable, SortFields.PUBLIC_LOCATION);

            return ResponseEntity.ok(
              locationPublicService.searchActiveLocations(
                name,
                city,
                connectorType,
                onlyAvailable,
                pageable
        )
);
    }
}