package com.proje.elektrikli_arac_sarj_sistemi.service.connector;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorResponse;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ConnectorMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.ConnectorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConnectorAdminService {

    private final ConnectorRepository connectorRepository;
    private final ConnectorMapper connectorMapper;

    public ConnectorAdminService(ConnectorRepository connectorRepository, ConnectorMapper connectorMapper) {
        this.connectorRepository = connectorRepository;
        this.connectorMapper = connectorMapper;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public Page<ConnectorResponse> search(
            ConnectorStandard standard, PowerType powerType, UUID evseId, Pageable pageable) {

        Specification<Connector> spec = Specification
                .where(ConnectorSpecification.hasStandard(standard))
                .and(ConnectorSpecification.hasPowerType(powerType))
                .and(ConnectorSpecification.belongsToEvse(evseId));

        return connectorRepository.findAll(spec, pageable)
                .map(connectorMapper::toResponse);
    }
}