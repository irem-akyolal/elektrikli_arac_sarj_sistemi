package com.proje.elektrikli_arac_sarj_sistemi.dto.provision;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ProvisionCreateRequest {

    @NotNull(message = "Şarj oturumu ID boş olamaz")
    private UUID chargingSessionId;

    @NotNull(message = "Talep edilen tutar boş olamaz")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tutar 0'dan büyük olmalı")
    private BigDecimal requestedAmount;

    public UUID getChargingSessionId() { return chargingSessionId; }
    public void setChargingSessionId(UUID chargingSessionId) { this.chargingSessionId = chargingSessionId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
}
