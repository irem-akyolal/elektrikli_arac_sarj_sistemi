package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.evse.EvseResponse;
import org.springframework.stereotype.Component;

@Component
public class EvseMapper {

    public EvseResponse toResponse(Evse evse) {
        return new EvseResponse(
                evse.getId(),
                evse.getOcpiEvseUid(),
                evse.getEvseId(),
                evse.getLocation().getId(),
                evse.getStatus()
        );
    }
}
