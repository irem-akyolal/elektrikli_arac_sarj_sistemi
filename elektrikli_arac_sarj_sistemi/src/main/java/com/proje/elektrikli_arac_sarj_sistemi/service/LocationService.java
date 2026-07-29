package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.LocationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Transactional
    public LocationResponse create(LocationCreateRequest request) {
        validateOcpiLocationId(request.getOcpiLocationId());

        Location location = locationMapper.toEntity(request);
        location.setActive(true);
        Location saved = locationRepository.save(location);
        return locationMapper.toResponse(saved);
    }

    public LocationResponse getById(UUID id) {
        Location location = findLocation(id);
        return locationMapper.toResponse(location);
    }

    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    public List<LocationResponse> getAllActive() {
    // Public website için — sadece aktif istasyonlar
    return locationRepository.findAllByActiveTrue()
            .stream()
            .map(locationMapper::toResponse) // Her bir entity'yi DTO'ya dönüştürür.
            .toList();
}

    @Transactional
    public void deactivate(UUID id) {
        Location location = findLocation(id);
        location.setActive(false);
        locationRepository.save(location);
    }

    // ============================
    // Private Methods
    // ============================

    private void validateOcpiLocationId(String ocpiLocationId) {
        if (locationRepository.existsByOcpiLocationId(ocpiLocationId)) {
            throw new BusinessRuleViolationException(
                    "LOCATION_ALREADY_EXISTS",
                    "Bu OCPI location ID zaten kayıtlı: " + ocpiLocationId
            );
        }
    }

    private Location findLocation(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("İstasyon bulunamadı: " + id));
    }
}
