package com.proje.elektrikli_arac_sarj_sistemi.dto.evse;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;

import java.util.UUID;

public class EvseResponse {

    private UUID id;
    private String ocpiEvseUid;
    private String evseId;
    private UUID locationId;
    private EvseStatus status;

    public EvseResponse(UUID id, String ocpiEvseUid, String evseId, UUID locationId, EvseStatus status) {
        this.id = id;
        this.ocpiEvseUid = ocpiEvseUid;
        this.evseId = evseId;
        this.locationId = locationId;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getOcpiEvseUid() { return ocpiEvseUid; }
    public String getEvseId() { return evseId; }
    public UUID getLocationId() { return locationId; }
    public EvseStatus getStatus() { return status; }
}


