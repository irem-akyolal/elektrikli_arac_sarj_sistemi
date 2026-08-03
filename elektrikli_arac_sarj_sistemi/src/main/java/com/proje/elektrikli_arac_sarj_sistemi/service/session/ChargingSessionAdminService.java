package com.proje.elektrikli_arac_sarj_sistemi.service.session;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ChargingSessionMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.ChargingSessionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChargingSessionAdminService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final ChargingSessionMapper chargingSessionMapper;

    public ChargingSessionAdminService(ChargingSessionRepository chargingSessionRepository,
                                        ChargingSessionMapper chargingSessionMapper) {
        this.chargingSessionRepository = chargingSessionRepository;
        this.chargingSessionMapper = chargingSessionMapper;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public Page<ChargingSessionResponse> search(
            SessionStatus status,
            String email,
            String plateNumber,
            LocalDateTime startedAfter,
            LocalDateTime startedBefore,
            Pageable pageable) {

        Specification<ChargingSession> spec = Specification
                .where(ChargingSessionSpecification.hasStatus(status))
                .and(ChargingSessionSpecification.hasEmail(email))
                .and(ChargingSessionSpecification.hasPlateNumber(plateNumber))
                .and(ChargingSessionSpecification.startedBetween(startedAfter, startedBefore));

        return chargingSessionRepository.findAll(spec, pageable)
                .map(chargingSessionMapper::toResponse);
    }
}