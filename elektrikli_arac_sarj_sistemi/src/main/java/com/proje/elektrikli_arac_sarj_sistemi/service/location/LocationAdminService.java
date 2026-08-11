package com.proje.elektrikli_arac_sarj_sistemi.service.location;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.audit.Auditable;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.location.LocationUpdateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.LocationMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.LocationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proje.elektrikli_arac_sarj_sistemi.audit.AuditLogService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationAdminService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LocationCoreService locationCoreService;
    private final AuditLogService auditLogService;
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @Auditable(action = AuditAction.CREATE, entityType = "LOCATION")
    @Transactional
    public LocationResponse create(LocationCreateRequest request) {
        locationCoreService.validateOcpiLocationId(request.getOcpiLocationId());
        Location location = locationMapper.toEntity(request);
        Location saved = locationRepository.save(location);
        return locationMapper.toResponse(saved);
    }


@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
@Transactional
public LocationResponse update(UUID id, LocationUpdateRequest request) {

    Location location = locationCoreService.findLocation(id);

    StringBuilder changes = new StringBuilder();

    if (request.getName() != null &&
            !request.getName().equals(location.getName())) {

        changes.append("name: '")
                .append(location.getName())
                .append("' → '")
                .append(request.getName())
                .append("'; ");

        location.setName(request.getName());
    }

    if (request.getAddress() != null &&
            !request.getAddress().equals(location.getAddress())) {

        changes.append("address: '")
                .append(location.getAddress())
                .append("' → '")
                .append(request.getAddress())
                .append("'; ");

        location.setAddress(request.getAddress());
    }

    if (request.getCity() != null &&
            !request.getCity().equals(location.getCity())) {

        changes.append("city: '")
                .append(location.getCity())
                .append("' → '")
                .append(request.getCity())
                .append("'; ");

        location.setCity(request.getCity());
    }

    if (request.getPostalCode() != null &&
            !request.getPostalCode().equals(location.getPostalCode())) {

        changes.append("postalCode: '")
                .append(location.getPostalCode())
                .append("' → '")
                .append(request.getPostalCode())
                .append("'; ");

        location.setPostalCode(request.getPostalCode());
    }

    if (request.getCountry() != null &&
            !request.getCountry().equals(location.getCountry())) {

        changes.append("country: '")
                .append(location.getCountry())
                .append("' → '")
                .append(request.getCountry())
                .append("'; ");

        location.setCountry(request.getCountry());
    }

    if (request.getLatitude() != null &&
            !request.getLatitude().equals(location.getLatitude())) {

        changes.append("latitude: '")
                .append(location.getLatitude())
                .append("' → '")
                .append(request.getLatitude())
                .append("'; ");

        location.setLatitude(request.getLatitude());
    }

    if (request.getLongitude() != null &&
            !request.getLongitude().equals(location.getLongitude())) {

        changes.append("longitude: '")
                .append(location.getLongitude())
                .append("' → '")
                .append(request.getLongitude())
                .append("'; ");

        location.setLongitude(request.getLongitude());
    }

    if (request.getTimeZone() != null &&
            !request.getTimeZone().equals(location.getTimeZone())) {

        changes.append("timeZone: '")
                .append(location.getTimeZone())
                .append("' → '")
                .append(request.getTimeZone())
                .append("'; ");

        location.setTimeZone(request.getTimeZone());
    }

    Location updated = locationRepository.save(location);

    // Gerçekten bir değişiklik olduysa audit log oluştur
    if (!changes.isEmpty()) {

        auditLogService.logManual(
                auditLogService.getCurrentUsername(),
                AuditAction.UPDATE,
                "LOCATION",
                id.toString(),
                changes.toString()
        );
    }

    return locationMapper.toResponse(updated);
}

    @PreAuthorize("hasRole('SUPER_ADMIN')") 
    @Transactional
    public LocationResponse activate(UUID id) {
        Location location = locationCoreService.findLocation(id);
        location.setActive(true);
        return locationMapper.toResponse(locationRepository.save(location));
    }
    
    @PreAuthorize("hasRole('SUPER_ADMIN')") 
    @Auditable(action = AuditAction.DEACTIVATE, entityType = "LOCATION")
    @Transactional
    public void deactivate(UUID id) {
        Location location = locationCoreService.findLocation(id);
        location.setActive(false);
        locationRepository.save(location);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public LocationResponse getById(UUID id) {
        Location location = locationCoreService.findLocation(id);
        return locationMapper.toResponse(location);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
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
