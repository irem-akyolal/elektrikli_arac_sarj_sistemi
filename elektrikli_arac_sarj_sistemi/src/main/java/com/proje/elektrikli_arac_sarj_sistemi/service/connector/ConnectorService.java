package com.proje.elektrikli_arac_sarj_sistemi.service.connector;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.audit.Auditable;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorUpdateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ConnectorMapper;
import com.proje.elektrikli_arac_sarj_sistemi.audit.AuditLogService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConnectorService {

    private final ConnectorRepository connectorRepository;
    private final EvseRepository evseRepository;
    private final ConnectorMapper connectorMapper;
    private final AuditLogService auditLogService;

    public ConnectorService(ConnectorRepository connectorRepository,
                             EvseRepository evseRepository,
                             ConnectorMapper connectorMapper,
                             AuditLogService auditLogService) {
        this.connectorRepository = connectorRepository;
        this.evseRepository = evseRepository;
        this.connectorMapper = connectorMapper;
        this.auditLogService = auditLogService;
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @Auditable(action = AuditAction.CREATE, entityType = "CONNECTOR")
    @Transactional
    public ConnectorResponse create(ConnectorCreateRequest request) {
        validateOcpiConnectorId(request.getOcpiConnectorId());

        Evse evse = evseRepository.findById(request.getEvseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EVSE bulunamadı: " + request.getEvseId()));

        Connector connector = new Connector();
        connector.setOcpiConnectorId(request.getOcpiConnectorId());
        connector.setEvse(evse);
        connector.setStandard(request.getStandard());
        connector.setFormat(request.getFormat());
        connector.setPowerType(request.getPowerType());
        connector.setMaxVoltage(request.getMaxVoltage());
        connector.setMaxAmperage(request.getMaxAmperage());
        connector.setMaxElectricPowerWatt(request.getMaxElectricPowerWatt());
        connector.setUnitPrice(request.getUnitPrice());

        Connector saved = connectorRepository.save(connector);
        return connectorMapper.toResponse(saved);
    }

    public ConnectorResponse getById(UUID id) {
        Connector connector = findConnector(id);
        return connectorMapper.toResponse(connector);
    }

    public List<ConnectorResponse> getByEvseId(UUID evseId) { // ID'si verilen EVSE'ye ait tüm Connector'ları veritabanından bulur, her birini DTO'ya çevirir ve liste olarak döndürür.
        return connectorRepository.findByEvseId(evseId)
                .stream()
                .map(connectorMapper::toResponse)
                .toList();
    }

@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
@Transactional
public ConnectorResponse updateDetails(UUID id, ConnectorUpdateRequest request) {

    Connector connector = findConnector(id);

    StringBuilder changes = new StringBuilder();

    if (request.getStandard() != null &&
            request.getStandard() != connector.getStandard()) {

        changes.append("standard: '")
                .append(connector.getStandard())
                .append("' → '")
                .append(request.getStandard())
                .append("'; ");

        connector.setStandard(request.getStandard());
    }

    if (request.getFormat() != null &&
            request.getFormat() != connector.getFormat()) {

        changes.append("format: '")
                .append(connector.getFormat())
                .append("' → '")
                .append(request.getFormat())
                .append("'; ");

        connector.setFormat(request.getFormat());
    }

    if (request.getPowerType() != null &&
            request.getPowerType() != connector.getPowerType()) {

        changes.append("powerType: '")
                .append(connector.getPowerType())
                .append("' → '")
                .append(request.getPowerType())
                .append("'; ");

        connector.setPowerType(request.getPowerType());
    }

    if (request.getMaxVoltage() != null &&
            !request.getMaxVoltage().equals(connector.getMaxVoltage())) {

        changes.append("maxVoltage: '")
                .append(connector.getMaxVoltage())
                .append("' → '")
                .append(request.getMaxVoltage())
                .append("'; ");

        connector.setMaxVoltage(request.getMaxVoltage());
    }

    if (request.getMaxAmperage() != null &&
            !request.getMaxAmperage().equals(connector.getMaxAmperage())) {

        changes.append("maxAmperage: '")
                .append(connector.getMaxAmperage())
                .append("' → '")
                .append(request.getMaxAmperage())
                .append("'; ");

        connector.setMaxAmperage(request.getMaxAmperage());
    }

    if (request.getMaxElectricPowerWatt() != null &&
            !request.getMaxElectricPowerWatt().equals(connector.getMaxElectricPowerWatt())) {

        changes.append("maxElectricPowerWatt: '")
                .append(connector.getMaxElectricPowerWatt())
                .append("' → '")
                .append(request.getMaxElectricPowerWatt())
                .append("'; ");

        connector.setMaxElectricPowerWatt(request.getMaxElectricPowerWatt());
    }

    Connector saved = connectorRepository.save(connector);

    if (changes.length() > 0) {
        auditLogService.logManual(
                auditLogService.getCurrentUsername(),
                AuditAction.UPDATE,
                "CONNECTOR",
                id.toString(),
                changes.toString()
        );
    }

    return connectorMapper.toResponse(saved);
}
     
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @Auditable(action = AuditAction.UPDATE, entityType = "CONNECTOR")
    @Transactional
    public ConnectorResponse updateUnitPrice(UUID id, java.math.BigDecimal newPrice) {
        Connector connector = findConnector(id);

        if (newPrice.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException(
                    "INVALID_PRICE",
                    "Birim fiyat 0'dan büyük olmalı"
            );
        }

        connector.setUnitPrice(newPrice);
        Connector saved = connectorRepository.save(connector);
        return connectorMapper.toResponse(saved);
    }

    // ============================
    // Private Methods
    // ============================

    private void validateOcpiConnectorId(String ocpiConnectorId) {
        if (connectorRepository.existsByOcpiConnectorId(ocpiConnectorId)) {
            throw new BusinessRuleViolationException(
                    "CONNECTOR_ALREADY_EXISTS",
                    "Bu OCPI Connector ID zaten kayıtlı: " + ocpiConnectorId
            );
        }
    }

    private Connector findConnector(UUID id) {
        return connectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Konnektör bulunamadı: " + id));
    }
}
