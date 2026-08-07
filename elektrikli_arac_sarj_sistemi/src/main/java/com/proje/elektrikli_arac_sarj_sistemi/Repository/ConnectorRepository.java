package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.projection.ConnectorAvailabilityProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConnectorRepository extends JpaRepository<Connector, UUID>, JpaSpecificationExecutor<Connector> {

    Optional<Connector> findByOcpiConnectorId(String ocpiConnectorId);

    boolean existsByOcpiConnectorId(String ocpiConnectorId);

    List<Connector> findByEvseId(UUID evseId);


    @Query("""
        SELECT c.evse.location.id AS locationId,
               CAST(c.powerType AS string) AS powerType,
               COUNT(c) AS totalCount,
               SUM(CASE WHEN c.evse.status = com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus.AVAILABLE THEN 1L ELSE 0L END) AS availableCount,
               AVG(c.unitPrice) AS unitPrice
        FROM Connector c
        WHERE c.evse.location.id IN :locationIds
        GROUP BY c.evse.location.id, c.powerType
        """)
    List<ConnectorAvailabilityProjection> findAvailabilitySummaries(@Param("locationIds") List<UUID> locationIds);


    @Query("SELECT COUNT(c) FROM Connector c WHERE c.evse.status = com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus.AVAILABLE")
    long countAvailable(); // admin dashbord için


// adm,n dashborad da kaç tane unknown power type, format ve standard var onu göstermek için
long countByStandard(com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard standard);
long countByFormat(com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorFormat format);
long countByPowerType(com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType powerType);
}