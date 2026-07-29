package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import org.springframework.stereotype.Component;

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
        return location;
    }

    public LocationResponse toResponse(Location location) {
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
                location.isActive()
        );
    }
}
