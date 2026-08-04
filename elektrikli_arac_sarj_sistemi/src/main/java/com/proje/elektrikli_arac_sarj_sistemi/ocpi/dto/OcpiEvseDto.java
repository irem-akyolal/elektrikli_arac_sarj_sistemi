package com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OcpiEvseDto {

    private String uid;

    @JsonProperty("evse_id")
    private String evseId;

    private String status;
    private List<OcpiConnectorDto> connectors;

    @JsonProperty("last_updated")
    private String lastUpdated;

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEvseId() { return evseId; }
    public void setEvseId(String evseId) { this.evseId = evseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OcpiConnectorDto> getConnectors() { return connectors; }
    public void setConnectors(List<OcpiConnectorDto> connectors) { this.connectors = connectors; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}