package com.proje.elektrikli_arac_sarj_sistemi.service.evse;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseResponse;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.EvseMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.EvseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EvseAdminService {

    private final EvseRepository evseRepository;
    private final EvseMapper evseMapper;

    public EvseAdminService(EvseRepository evseRepository, EvseMapper evseMapper) {
        this.evseRepository = evseRepository;
        this.evseMapper = evseMapper;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public Page<EvseResponse> search(EvseStatus status, UUID locationId, Pageable pageable) {
        Specification<Evse> spec = Specification
                .where(EvseSpecification.hasStatus(status))
                .and(EvseSpecification.belongsToLocation(locationId));

        return evseRepository.findAll(spec, pageable)
                .map(evseMapper::toResponse);
    }
}