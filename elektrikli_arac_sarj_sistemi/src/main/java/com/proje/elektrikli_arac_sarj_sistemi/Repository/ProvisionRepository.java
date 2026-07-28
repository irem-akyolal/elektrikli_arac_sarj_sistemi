package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProvisionRepository extends JpaRepository<Provision, UUID> {

    Optional<Provision> findByChargingSessionId(UUID chargingSessionId);

    // Scheduler'ın periyodik taradığı, hâlâ açık olan provizyonlar
    List<Provision> findByStatus(ProvisionStatus status);
}