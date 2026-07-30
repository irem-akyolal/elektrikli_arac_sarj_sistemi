package com.proje.elektrikli_arac_sarj_sistemi.service.location;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationDetailResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.LocationMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.LocationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationPublicService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LocationCoreService locationCoreService;

    @Transactional(readOnly = true)
    public List<LocationResponse> getActiveLocations() {
        return locationRepository.findAllByActiveTrue()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    // Tekil detay — connector listesi burada yükleniyor, @Transactional ile güvenli
    @Transactional(readOnly = true)
    public LocationDetailResponse getLocationDetail(UUID id) {
        Location location = locationCoreService.findLocation(id);
        locationCoreService.validateLocationActive(location);
        return locationMapper.toDetailResponse(location);
    }

    @Transactional(readOnly = true)
    public Page<LocationResponse> searchActiveLocations(
            String name, String city, String connectorType, Boolean onlyAvailable, Pageable pageable) {

        Specification<Location> spec = Specification
                .where(LocationSpecification.hasName(name))
                .and(LocationSpecification.hasCity(city))
                .and(LocationSpecification.isActive(true));

        if (StringUtils.hasText(connectorType)) {
            spec = spec.and(LocationSpecification.hasConnectorType(connectorType));
        }

        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and(LocationSpecification.hasAvailableEvses());
        }

        return locationRepository.findAll(spec, pageable)
                .map(locationMapper::toResponse);
    }
}