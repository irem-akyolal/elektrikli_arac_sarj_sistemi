package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {

    private UUID id;
    private String ocpiLocationId;
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private String timeZone;
    private boolean active;
    private List<ConnectorAvailabilitySummary> availability; 
}
