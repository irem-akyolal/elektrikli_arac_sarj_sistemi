package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID>, JpaSpecificationExecutor<ChargingSession> {

    Optional<ChargingSession> findByOcpiSessionId(String ocpiSessionId);

    // Scheduler için: şu an aktif (devam eden) tüm session'ları bulmak
    List<ChargingSession> findByStatus(SessionStatus status);

    List<ChargingSession> findByConnectorId(UUID connectorId);

    long countByStatus(SessionStatus status);

    @Query("SELECT COUNT(cs) FROM ChargingSession cs WHERE cs.status = 'COMPLETED' AND cs.completedAt >= :startOfDay")
    long countCompletedSince(@Param("startOfDay") LocalDateTime startOfDay);

    

    
}