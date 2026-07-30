package com.proje.elektrikli_arac_sarj_sistemi.service.location;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationCoreService {

    private final LocationRepository locationRepository;

    public Location findLocation(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İstasyon bulunamadı: " + id));
    }

    public void validateOcpiLocationId(String ocpiLocationId) {
        if (locationRepository.existsByOcpiLocationId(ocpiLocationId)) {
            throw new BusinessRuleViolationException(
                    "LOCATION_ALREADY_EXISTS",
                    "Bu OCPI location ID zaten kayıtlı: " + ocpiLocationId
            );
        }
    }

    public void validateLocationActive(Location location) {
        if (!location.isActive()) {
            throw new BusinessRuleViolationException(
                    "LOCATION_NOT_ACTIVE",
                    "Bu istasyon şu anda aktif değil: " + location.getId()
            );
        }
    }
}
