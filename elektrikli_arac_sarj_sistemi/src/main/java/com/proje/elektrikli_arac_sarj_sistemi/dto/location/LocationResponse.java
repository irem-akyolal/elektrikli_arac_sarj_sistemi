package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import java.util.UUID;

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

    public LocationResponse(UUID id, String ocpiLocationId, String name, String address,
                             String city, String postalCode, String country,
                             Double latitude, Double longitude, String timeZone, boolean active) {
        this.id = id;
        this.ocpiLocationId = ocpiLocationId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
        this.active = active;
    }

    public UUID getId() { return id; }
    public String getOcpiLocationId() { return ocpiLocationId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getTimeZone() { return timeZone; }
    public boolean isActive() { return active; }
}