package com.proje.elektrikli_arac_sarj_sistemi.service.location;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationUpdateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.LocationMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.LocationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationAdminService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LocationCoreService locationCoreService;

    @Transactional
    public LocationResponse create(LocationCreateRequest request) {
        locationCoreService.validateOcpiLocationId(request.getOcpiLocationId());
        Location location = locationMapper.toEntity(request);
        Location saved = locationRepository.save(location);
        return locationMapper.toResponse(saved);
    }

    @Transactional
    public LocationResponse update(UUID id, LocationUpdateRequest request) {
        Location location = locationCoreService.findLocation(id);

        if (request.getName() != null) location.setName(request.getName());
        if (request.getAddress() != null) location.setAddress(request.getAddress());
        if (request.getCity() != null) location.setCity(request.getCity());
        if (request.getPostalCode() != null) location.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) location.setCountry(request.getCountry());
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getTimeZone() != null) location.setTimeZone(request.getTimeZone());

        Location updated = locationRepository.save(location);
        return locationMapper.toResponse(updated);
    }

    @Transactional
    public LocationResponse activate(UUID id) {
        Location location = locationCoreService.findLocation(id);
        location.setActive(true);
        return locationMapper.toResponse(locationRepository.save(location));
    }

    @Transactional
    public void deactivate(UUID id) {
        Location location = locationCoreService.findLocation(id);
        location.setActive(false);
        locationRepository.save(location);
    }

    @Transactional(readOnly = true)
    public LocationResponse getById(UUID id) {
        Location location = locationCoreService.findLocation(id);
        return locationMapper.toResponse(location);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<LocationResponse> getAllWithFilters(String name, String city, Boolean active, Pageable pageable) {
        Specification<Location> spec = Specification
                .where(LocationSpecification.hasName(name))
                .and(LocationSpecification.hasCity(city))
                .and(LocationSpecification.isActive(active));

        return locationRepository.findAll(spec, pageable)
                .map(locationMapper::toResponse);
    }
}
