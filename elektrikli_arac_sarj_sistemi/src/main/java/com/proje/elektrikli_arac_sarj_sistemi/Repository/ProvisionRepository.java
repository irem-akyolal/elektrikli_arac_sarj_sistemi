package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProvisionRepository extends JpaRepository<Provision, UUID>, JpaSpecificationExecutor<Provision> {

    Optional<Provision> findByChargingSessionId(UUID chargingSessionId);

    // Scheduler'ın periyodik taradığı, hâlâ açık olan provizyonlar
    List<Provision> findByStatus(ProvisionStatus status);

     List<Provision> findByStatusAndCreatedAtBefore(ProvisionStatus status, LocalDateTime dateTime); // scheduler için: belirli bir tarihten önce oluşturulmuş ve hâlâ ACTIVE durumunda olan provizyonları bulmak yani zaman aşımı kontrolü için açık kalmış provizyon temzliği için
}