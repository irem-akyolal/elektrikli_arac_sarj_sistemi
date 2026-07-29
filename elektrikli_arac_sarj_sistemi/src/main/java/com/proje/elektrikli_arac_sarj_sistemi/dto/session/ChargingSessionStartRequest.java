package com.proje.elektrikli_arac_sarj_sistemi.dto.session;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ChargingSessionStartRequest {

    @NotNull(message = "Konnektör ID boş olamaz")
    private UUID connectorId;

    @NotBlank(message = "Plaka boş olamaz")
    private String plateNumber;

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    public UUID getConnectorId() { return connectorId; }
    public void setConnectorId(UUID connectorId) { this.connectorId = connectorId; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}