package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByOcpiLocationId(String ocpiLocationId);

    boolean existsByOcpiLocationId(String ocpiLocationId);

    List<Location> findAllByActiveTrue();// kullanıcıların sadece aktif istasyonları görebilmesi için yazıldı bu sorgu.
}
