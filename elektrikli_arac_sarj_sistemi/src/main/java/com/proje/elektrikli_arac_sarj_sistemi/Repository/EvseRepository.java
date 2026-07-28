package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvseRepository extends JpaRepository<Evse, UUID> {

    Optional<Evse> findByOcpiEvseUid(String ocpiEvseUid);

    List<Evse> findByLocationId(UUID locationId);
}