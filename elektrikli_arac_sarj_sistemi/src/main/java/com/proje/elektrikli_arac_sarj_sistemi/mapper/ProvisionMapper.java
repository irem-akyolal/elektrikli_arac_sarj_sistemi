package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionResponse;
import org.springframework.stereotype.Component;

@Component
public class ProvisionMapper {

    public ProvisionResponse toResponse(Provision provision) {
        return new ProvisionResponse(
                provision.getId(),
                provision.getChargingSession().getId(),
                provision.getRequestedAmount(),
                provision.getStatus(),
                provision.getProviderReferenceId(),
                provision.getClosedAt()
        );
    }
}