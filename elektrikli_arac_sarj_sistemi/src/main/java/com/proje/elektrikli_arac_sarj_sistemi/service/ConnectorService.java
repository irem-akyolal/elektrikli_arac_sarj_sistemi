package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ConnectorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConnectorService {

    private final ConnectorRepository connectorRepository;
    private final EvseRepository evseRepository;
    private final ConnectorMapper connectorMapper;

    public ConnectorService(ConnectorRepository connectorRepository,
                             EvseRepository evseRepository,
                             ConnectorMapper connectorMapper) {
        this.connectorRepository = connectorRepository;
        this.evseRepository = evseRepository;
        this.connectorMapper = connectorMapper;
    }

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
