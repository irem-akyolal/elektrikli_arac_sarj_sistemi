package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConnectorRepository extends JpaRepository<Connector, UUID> {

    Optional<Connector> findByOcpiConnectorId(String ocpiConnectorId);

    boolean existsByOcpiConnectorId(String ocpiConnectorId);

    List<Connector> findByEvseId(UUID evseId);
}