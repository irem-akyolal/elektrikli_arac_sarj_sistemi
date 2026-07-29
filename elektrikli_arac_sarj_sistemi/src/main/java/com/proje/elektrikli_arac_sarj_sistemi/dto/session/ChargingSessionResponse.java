package com.proje.elektrikli_arac_sarj_sistemi.dto.session;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChargingSessionResponse {

    private UUID id;
    private UUID connectorId;
    private String plateNumber;
    private String email;
    private String ocpiSessionId;
    private SessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime connectorRemovedAt;
    private BigDecimal energyConsumedKwh;

    public ChargingSessionResponse(UUID id, UUID connectorId, String plateNumber, String email,
                                    String ocpiSessionId, SessionStatus status,
                                    LocalDateTime startedAt, LocalDateTime completedAt,
                                    LocalDateTime connectorRemovedAt, BigDecimal energyConsumedKwh) {
        this.id = id;
        this.connectorId = connectorId;
        this.plateNumber = plateNumber;
        this.email = email;
        this.ocpiSessionId = ocpiSessionId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.connectorRemovedAt = connectorRemovedAt;
        this.energyConsumedKwh = energyConsumedKwh;
    }

    public UUID getId() { return id; }
    public UUID getConnectorId() { return connectorId; }
    public String getPlateNumber() { return plateNumber; }
    public String getEmail() { return email; }
    public String getOcpiSessionId() { return ocpiSessionId; }
    public SessionStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getConnectorRemovedAt() { return connectorRemovedAt; }
    public BigDecimal getEnergyConsumedKwh() { return energyConsumedKwh; }
}