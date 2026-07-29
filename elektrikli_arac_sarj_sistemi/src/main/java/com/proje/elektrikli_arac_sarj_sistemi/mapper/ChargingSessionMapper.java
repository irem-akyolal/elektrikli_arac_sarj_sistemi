package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionResponse;
import org.springframework.stereotype.Component;

@Component
public class ChargingSessionMapper {

    public ChargingSessionResponse toResponse(ChargingSession session) {
        return new ChargingSessionResponse(
                session.getId(),
                session.getConnector().getId(),
                session.getPlateNumber(),
                session.getEmail(),
                session.getOcpiSessionId(),
                session.getStatus(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getConnectorRemovedAt(),
                session.getEnergyConsumedKwh()
        );
    }
}
