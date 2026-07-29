package com.proje.elektrikli_arac_sarj_sistemi.dto.connector;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorFormat;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;

import java.math.BigDecimal;
import java.util.UUID;

public class ConnectorResponse {

    private UUID id;
    private String ocpiConnectorId;
    private UUID evseId;
    private ConnectorStandard standard;
    private ConnectorFormat format;
    private PowerType powerType;
    private Integer maxVoltage;
    private Integer maxAmperage;
    private Integer maxElectricPowerWatt;
    private BigDecimal unitPrice;

    public ConnectorResponse(UUID id, String ocpiConnectorId, UUID evseId,
                              ConnectorStandard standard, ConnectorFormat format, PowerType powerType,
                              Integer maxVoltage, Integer maxAmperage, Integer maxElectricPowerWatt,
                              BigDecimal unitPrice) {
        this.id = id;
        this.ocpiConnectorId = ocpiConnectorId;
        this.evseId = evseId;
        this.standard = standard;
        this.format = format;
        this.powerType = powerType;
        this.maxVoltage = maxVoltage;
        this.maxAmperage = maxAmperage;
        this.maxElectricPowerWatt = maxElectricPowerWatt;
        this.unitPrice = unitPrice;
    }

    public UUID getId() { return id; }
    public String getOcpiConnectorId() { return ocpiConnectorId; }
    public UUID getEvseId() { return evseId; }
    public ConnectorStandard getStandard() { return standard; }
    public ConnectorFormat getFormat() { return format; }
    public PowerType getPowerType() { return powerType; }
    public Integer getMaxVoltage() { return maxVoltage; }
    public Integer getMaxAmperage() { return maxAmperage; }
    public Integer getMaxElectricPowerWatt() { return maxElectricPowerWatt; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
