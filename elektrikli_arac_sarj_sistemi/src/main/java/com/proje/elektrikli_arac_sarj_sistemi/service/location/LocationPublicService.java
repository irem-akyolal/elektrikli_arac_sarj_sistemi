package com.proje.elektrikli_arac_sarj_sistemi.service.location;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.projection.ConnectorAvailabilityProjection;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.ConnectorAvailabilitySummary;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationPublicService {

    private final LocationRepository locationRepository;
    private final ConnectorRepository connectorRepository;
    private final LocationMapper locationMapper;
    private final LocationCoreService locationCoreService;

    @Transactional(readOnly = true)
    public List<LocationResponse> getActiveLocations() {
        List<Location> locations = locationRepository.findAllByActiveTrue();
        return buildResponsesWithAvailability(locations);
    }

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

        Page<Location> page = locationRepository.findAll(spec, pageable);

        // Page içindeki location'ları response'a çevirirken, availability bilgisini de ekliyoruz
        Map<UUID, List<ConnectorAvailabilitySummary>> availabilityMap = fetchAvailabilityMap(
                page.getContent().stream().map(Location::getId).toList()
        );

        return page.map(location -> locationMapper.toResponse(
                location,
                availabilityMap.getOrDefault(location.getId(), List.of())
        ));
    }


      // konum tabanlı arama için yeni bir method ekledik.
      @Transactional(readOnly = true)
      public List<LocationResponse> getNearbyLocations(double latitude, double longitude, double radiusKm) {
       List<Location> locations = locationRepository.findNearby(latitude, longitude, radiusKm);
          return buildResponsesWithAvailability(locations); // az önce yazdığımız availability mantığını da kullanıyoruz
}


    // ============================
    // Private Methods
    // ============================

    private List<LocationResponse> buildResponsesWithAvailability(List<Location> locations) {
        List<UUID> locationIds = locations.stream().map(Location::getId).toList();
        Map<UUID, List<ConnectorAvailabilitySummary>> availabilityMap = fetchAvailabilityMap(locationIds);

        return locations.stream()
                .map(location -> locationMapper.toResponse(
                        location,
                        availabilityMap.getOrDefault(location.getId(), List.of())
                ))
                .toList();
    }

    // TEK sorguyla tüm location'ların availability özetini çek, locationId'ye göre grupla
    private Map<UUID, List<ConnectorAvailabilitySummary>> fetchAvailabilityMap(List<UUID> locationIds) {
        if (locationIds.isEmpty()) {
            return Map.of();
        }

        List<ConnectorAvailabilityProjection> projections =
                connectorRepository.findAvailabilitySummaries(locationIds);

        return projections.stream()
                .collect(Collectors.groupingBy(
                        ConnectorAvailabilityProjection::getLocationId,
                        Collectors.mapping(
                                p -> new ConnectorAvailabilitySummary(
                                        p.getPowerType(),
                                        p.getTotalCount(),
                                        p.getAvailableCount(),
                                        p.getUnitPrice()
                                ),
                                Collectors.toList()
                        )
                ));
    }
}