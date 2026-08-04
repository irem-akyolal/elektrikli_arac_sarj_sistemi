package com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OcpiLocationDto {

    private String id;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("party_id")
    private String partyId;

    private String name;
    private String address;
    private String city;

    @JsonProperty("postal_code")
    private String postalCode;

    private String country;
    private OcpiCoordinatesDto coordinates;
    private List<OcpiEvseDto> evses;

    @JsonProperty("last_updated")
    private String lastUpdated;

    // getter/setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getPartyId() { return partyId; }
    public void setPartyId(String partyId) { this.partyId = partyId; }
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
    public OcpiCoordinatesDto getCoordinates() { return coordinates; }
    public void setCoordinates(OcpiCoordinatesDto coordinates) { this.coordinates = coordinates; }
    public List<OcpiEvseDto> getEvses() { return evses; }
    public void setEvses(List<OcpiEvseDto> evses) { this.evses = evses; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}