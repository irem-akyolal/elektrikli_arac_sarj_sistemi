package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.ConnectorAvailabilitySummary;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.ConnectorSummary;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationDetailResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationMapper {

    public Location toEntity(LocationCreateRequest request) {
        Location location = new Location();
        location.setOcpiLocationId(request.getOcpiLocationId());
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setPostalCode(request.getPostalCode());
        location.setCountry(request.getCountry());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setTimeZone(request.getTimeZone());
        location.setActive(true);
        return location;
    }

    // availability parametresiz — boş liste ile, admin panelde availability gerekmeyecek
    public LocationResponse toResponse(Location location) {
        return toResponse(location, Collections.emptyList());
    }

    // availability doldurulmuş halde — public arama/liste ekranında kullanılacak
    public LocationResponse toResponse(Location location, List<ConnectorAvailabilitySummary> availability) {
        return new LocationResponse(
                location.getId(),
                location.getOcpiLocationId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.getPostalCode(),
                location.getCountry(),
                location.getLatitude(),
                location.getLongitude(),
                location.getTimeZone(),
                location.isActive(),
                availability
        );
    }

    public LocationDetailResponse toDetailResponse(Location location) {
        return new LocationDetailResponse(
                location.getId(),
                location.getOcpiLocationId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.getPostalCode(),
                location.getCountry(),
                location.getLatitude(),
                location.getLongitude(),
                location.getTimeZone(),
                location.isActive(),
                toConnectorSummaries(location)
        );
    }

    private List<ConnectorSummary> toConnectorSummaries(Location location) {
        return location.getEvses().stream()
                .flatMap(evse -> evse.getConnectors().stream())
                .map(this::toConnectorSummary)
                .collect(Collectors.toList());
    }

    private ConnectorSummary toConnectorSummary(Connector connector) {
        return new ConnectorSummary(
                connector.getOcpiConnectorId(),
                connector.getStandard() != null ? connector.getStandard().name() : null,
                connector.getEvse().getStatus() != null ? connector.getEvse().getStatus().name() : null,
                connector.getPowerType() != null ? connector.getPowerType().name() : null,
                connector.getMaxElectricPowerWatt(),
                connector.getUnitPrice()
        );
    }
}