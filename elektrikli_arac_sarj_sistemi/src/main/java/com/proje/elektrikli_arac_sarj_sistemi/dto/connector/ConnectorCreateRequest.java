package com.proje.elektrikli_arac_sarj_sistemi.dto.connector;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorFormat;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ConnectorCreateRequest {

    @NotBlank(message = "OCPI Connector ID boş olamaz")
    private String ocpiConnectorId;

    @NotNull(message = "EVSE ID boş olamaz")
    private UUID evseId;

    @NotNull(message = "Konnektör tipi (standard) boş olamaz")
    private ConnectorStandard standard;

    @NotNull(message = "Format boş olamaz")
    private ConnectorFormat format;

    @NotNull(message = "Güç tipi (powerType) boş olamaz")
    private PowerType powerType;

    private Integer maxVoltage;
    private Integer maxAmperage;
    private Integer maxElectricPowerWatt;

    @NotNull(message = "Birim fiyat boş olamaz")
    @DecimalMin(value = "0.0", inclusive = false, message = "Birim fiyat 0'dan büyük olmalı")
    private BigDecimal unitPrice;

    public String getOcpiConnectorId() { return ocpiConnectorId; }
    public void setOcpiConnectorId(String ocpiConnectorId) { this.ocpiConnectorId = ocpiConnectorId; }
    public UUID getEvseId() { return evseId; }
    public void setEvseId(UUID evseId) { this.evseId = evseId; }
    public ConnectorStandard getStandard() { return standard; }
    public void setStandard(ConnectorStandard standard) { this.standard = standard; }
    public ConnectorFormat getFormat() { return format; }
    public void setFormat(ConnectorFormat format) { this.format = format; }
    public PowerType getPowerType() { return powerType; }
    public void setPowerType(PowerType powerType) { this.powerType = powerType; }
    public Integer getMaxVoltage() { return maxVoltage; }
    public void setMaxVoltage(Integer maxVoltage) { this.maxVoltage = maxVoltage; }
    public Integer getMaxAmperage() { return maxAmperage; }
    public void setMaxAmperage(Integer maxAmperage) { this.maxAmperage = maxAmperage; }
    public Integer getMaxElectricPowerWatt() { return maxElectricPowerWatt; }
    public void setMaxElectricPowerWatt(Integer maxElectricPowerWatt) { this.maxElectricPowerWatt = maxElectricPowerWatt; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
