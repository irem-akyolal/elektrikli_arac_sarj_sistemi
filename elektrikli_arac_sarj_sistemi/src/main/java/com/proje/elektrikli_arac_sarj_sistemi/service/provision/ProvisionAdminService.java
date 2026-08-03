package com.proje.elektrikli_arac_sarj_sistemi.service.provision;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ProvisionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ProvisionMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.ProvisionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProvisionAdminService {

    private final ProvisionRepository provisionRepository;
    private final ProvisionMapper provisionMapper;

    public ProvisionAdminService(ProvisionRepository provisionRepository, ProvisionMapper provisionMapper) {
        this.provisionRepository = provisionRepository;
        this.provisionMapper = provisionMapper;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public Page<ProvisionResponse> search(
            ProvisionStatus status,
            LocalDateTime closedAfter,
            LocalDateTime closedBefore,
            Pageable pageable) {

        Specification<Provision> spec = Specification
                .where(ProvisionSpecification.hasStatus(status))
                .and(ProvisionSpecification.closedBetween(closedAfter, closedBefore));

        return provisionRepository.findAll(spec, pageable)
                .map(provisionMapper::toResponse);
    }
}
