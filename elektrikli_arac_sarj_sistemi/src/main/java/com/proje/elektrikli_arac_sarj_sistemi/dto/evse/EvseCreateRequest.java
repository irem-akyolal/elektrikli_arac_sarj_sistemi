package com.proje.elektrikli_arac_sarj_sistemi.dto.evse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class EvseCreateRequest {

    @NotBlank(message = "OCPI EVSE UID boş olamaz")
    private String ocpiEvseUid;

    private String evseId;

    @NotNull(message = "Location ID boş olamaz")
    private UUID locationId;

    public String getOcpiEvseUid() { return ocpiEvseUid; }
    public void setOcpiEvseUid(String ocpiEvseUid) { this.ocpiEvseUid = ocpiEvseUid; }
    public String getEvseId() { return evseId; }
    public void setEvseId(String evseId) { this.evseId = evseId; }
    public UUID getLocationId() { return locationId; }
    public void setLocationId(UUID locationId) { this.locationId = locationId; }
}