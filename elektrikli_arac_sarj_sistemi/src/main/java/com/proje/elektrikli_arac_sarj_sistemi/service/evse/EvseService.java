package com.proje.elektrikli_arac_sarj_sistemi.service.evse;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.audit.Auditable;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.EvseMapper;
import com.proje.elektrikli_arac_sarj_sistemi.audit.AuditLogService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EvseService {

    private final EvseRepository evseRepository;
    private final LocationRepository locationRepository;
    private final EvseMapper evseMapper;
    private final AuditLogService auditLogService;

    public EvseService(EvseRepository evseRepository,
                        LocationRepository locationRepository,
                        EvseMapper evseMapper,
                        AuditLogService auditLogService) {
        this.evseRepository = evseRepository;
        this.locationRepository = locationRepository;
        this.evseMapper = evseMapper;
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @Auditable(action = AuditAction.CREATE, entityType = "EVSE")
    @Transactional
    public EvseResponse create(EvseCreateRequest request) {
        validateOcpiEvseUid(request.getOcpiEvseUid());

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "İstasyon bulunamadı: " + request.getLocationId()));

        Evse evse = new Evse();
        evse.setOcpiEvseUid(request.getOcpiEvseUid());
        evse.setEvseId(request.getEvseId());
        evse.setLocation(location);
        evse.setStatus(EvseStatus.AVAILABLE); // yeni EVSE varsayılan olarak müsait başlıyor — iş kararı, burada

        Evse saved = evseRepository.save(evse);
        return evseMapper.toResponse(saved);
    }

    public EvseResponse getById(UUID id) {
        Evse evse = findEvse(id);
        return evseMapper.toResponse(evse);
    }

    public List<EvseResponse> getByLocationId(UUID locationId) {
        return evseRepository.findByLocationId(locationId)
                .stream()
                .map(evseMapper::toResponse)
                .toList();
    }
    
   @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
   @Transactional
   public EvseResponse updateStatus(UUID id, EvseStatus newStatus) {

    Evse evse = findEvse(id);

    EvseStatus oldStatus = evse.getStatus();

    if (oldStatus == newStatus) {
        return evseMapper.toResponse(evse);
    }

    evse.setStatus(newStatus);

    Evse saved = evseRepository.save(evse);

    String details = String.format(
            "EVSE status changed: '%s' → '%s'",
            oldStatus,
            newStatus
    );

    auditLogService.logManual(
            auditLogService.getCurrentUsername(),
            AuditAction.UPDATE,
            "EVSE",
            id.toString(),
            details
    );

    return evseMapper.toResponse(saved);
}

    // ============================
    // Private Methods
    // ============================

    private void validateOcpiEvseUid(String ocpiEvseUid) {
        if (evseRepository.existsByOcpiEvseUid(ocpiEvseUid)) {
            throw new BusinessRuleViolationException(
                    "EVSE_ALREADY_EXISTS",
                    "Bu OCPI EVSE UID zaten kayıtlı: " + ocpiEvseUid
            );
        }
    }

    private Evse findEvse(UUID id) {
        return evseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EVSE bulunamadı: " + id));
    }
}
