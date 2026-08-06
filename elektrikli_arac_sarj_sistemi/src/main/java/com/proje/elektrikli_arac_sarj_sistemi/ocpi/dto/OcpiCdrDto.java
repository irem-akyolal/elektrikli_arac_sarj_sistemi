package com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OcpiCdrDto {

    private String id;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("total_energy")
    private Double totalEnergy;

    @JsonProperty("total_time")
    private Double totalTime;

    @JsonProperty("start_date_time")
    private String startDateTime;

    @JsonProperty("end_date_time")
    private String endDateTime;

    @JsonProperty("total_cost")
    private OcpiCdrCostDto totalCost;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Double getTotalEnergy() { return totalEnergy; }
    public void setTotalEnergy(Double totalEnergy) { this.totalEnergy = totalEnergy; }
    public Double getTotalTime() { return totalTime; }
    public void setTotalTime(Double totalTime) { this.totalTime = totalTime; }
    public String getStartDateTime() { return startDateTime; }
    public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }
    public String getEndDateTime() { return endDateTime; }
    public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }
    public OcpiCdrCostDto getTotalCost() { return totalCost; }
    public void setTotalCost(OcpiCdrCostDto totalCost) { this.totalCost = totalCost; }
}