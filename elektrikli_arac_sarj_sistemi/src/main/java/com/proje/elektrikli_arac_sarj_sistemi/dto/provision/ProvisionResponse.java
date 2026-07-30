package com.proje.elektrikli_arac_sarj_sistemi.dto.provision;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProvisionResponse {

    private UUID id;
    private UUID chargingSessionId;
    private BigDecimal requestedAmount;
    private ProvisionStatus status;
    private String providerReferenceId;
    private LocalDateTime closedAt;

    public ProvisionResponse(UUID id, UUID chargingSessionId, BigDecimal requestedAmount,
                              ProvisionStatus status, String providerReferenceId, LocalDateTime closedAt) {
        this.id = id;
        this.chargingSessionId = chargingSessionId;
        this.requestedAmount = requestedAmount;
        this.status = status;
        this.providerReferenceId = providerReferenceId;
        this.closedAt = closedAt;
    }

    public UUID getId() { return id; }
    public UUID getChargingSessionId() { return chargingSessionId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public ProvisionStatus getStatus() { return status; }
    public String getProviderReferenceId() { return providerReferenceId; }
    public LocalDateTime getClosedAt() { return closedAt; }
}