package com.proje.elektrikli_arac_sarj_sistemi.dto.connector;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorFormat;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;

public class ConnectorUpdateRequest {

    private ConnectorStandard standard;
    private ConnectorFormat format;
    private PowerType powerType;
    private Integer maxVoltage;
    private Integer maxAmperage;
    private Integer maxElectricPowerWatt;

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
}