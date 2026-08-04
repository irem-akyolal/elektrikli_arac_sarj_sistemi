package com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OcpiConnectorDto {

    private String id;
    private String standard;
    private String format;

    @JsonProperty("power_type")
    private String powerType;

    @JsonProperty("max_voltage")
    private Integer maxVoltage;

    @JsonProperty("max_amperage")
    private Integer maxAmperage;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getPowerType() { return powerType; }
    public void setPowerType(String powerType) { this.powerType = powerType; }
    public Integer getMaxVoltage() { return maxVoltage; }
    public void setMaxVoltage(Integer maxVoltage) { this.maxVoltage = maxVoltage; }
    public Integer getMaxAmperage() { return maxAmperage; }
    public void setMaxAmperage(Integer maxAmperage) { this.maxAmperage = maxAmperage; }
}