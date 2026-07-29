package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationCreateRequest {

    @NotBlank(message = "OCPI location ID boş olamaz")
    private String ocpiLocationId;

    @NotBlank(message = "İsim boş olamaz")
    private String name;

    @NotBlank(message = "Adres boş olamaz")
    private String address;

    private String city;
    private String postalCode;
    private String country;

    @NotNull(message = "Enlem (latitude) boş olamaz")
    private Double latitude;

    @NotNull(message = "Boylam (longitude) boş olamaz")
    private Double longitude;

    private String timeZone;

    public String getOcpiLocationId() { return ocpiLocationId; }
    public void setOcpiLocationId(String ocpiLocationId) { this.ocpiLocationId = ocpiLocationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
}