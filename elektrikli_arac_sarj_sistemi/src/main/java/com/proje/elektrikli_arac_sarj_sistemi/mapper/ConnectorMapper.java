package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.dto.connector.ConnectorResponse;
import org.springframework.stereotype.Component;

@Component
public class ConnectorMapper {

    public ConnectorResponse toResponse(Connector connector) {
        return new ConnectorResponse(
                connector.getId(),
                connector.getOcpiConnectorId(),
                connector.getEvse().getId(),
                connector.getStandard(),
                connector.getFormat(),
                connector.getPowerType(),
                connector.getMaxVoltage(),
                connector.getMaxAmperage(),
                connector.getMaxElectricPowerWatt(),
                connector.getUnitPrice()
        );
    }
}
