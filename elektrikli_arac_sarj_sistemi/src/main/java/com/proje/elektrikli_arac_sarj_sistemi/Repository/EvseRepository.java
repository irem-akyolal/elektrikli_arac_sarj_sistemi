package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvseRepository extends JpaRepository<Evse, UUID>, JpaSpecificationExecutor<Evse> {

    Optional<Evse> findByOcpiEvseUid(String ocpiEvseUid);

    boolean existsByOcpiEvseUid(String ocpiEvseUid);

    List<Evse> findByLocationId(UUID locationId);
}