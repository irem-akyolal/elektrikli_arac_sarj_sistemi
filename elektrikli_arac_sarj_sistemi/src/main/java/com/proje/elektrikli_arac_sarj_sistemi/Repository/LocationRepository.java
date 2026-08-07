package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID>, JpaSpecificationExecutor<Location> {

    Optional<Location> findByOcpiLocationId(String ocpiLocationId);

    boolean existsByOcpiLocationId(String ocpiLocationId);

    List<Location> findAllByActiveTrue();

    long countByActiveTrue(); // admin dashborad için eklendi aktif olan location sayısını göstermek için

    //JPArepository zaten toplam lokasyon sayısnı veriyor count() metodu ile


    // Konum tabanlı arama için native query neden sql çünkü acos, radians, sin, cos gibi trigonometrik fonksiyonlar, JPQL'in standart fonksiyon setinde yok — bunlar veritabanının kendi SQL fonksiyonları, JPA'nın soyutlama katmanının ötesinde. Bu yüzden nativeQuery = true ile doğrudan PostgreSQL'in SQL'ini yazdım.
   @Query(value = """
    SELECT * FROM (
        SELECT l.*,
               (6371 * acos(
                   cos(radians(:lat)) * cos(radians(l.latitude)) *
                   cos(radians(l.longitude) - radians(:lng)) +
                   sin(radians(:lat)) * sin(radians(l.latitude))
               )) AS distance_km
        FROM locations l
        WHERE l.active = true
    ) AS with_distance
    WHERE distance_km <= :radiusKm
    ORDER BY distance_km ASC
    """, nativeQuery = true)
List<Location> findNearby(@Param("lat") double latitude,
                           @Param("lng") double longitude,
                           @Param("radiusKm") double radiusKm);
}

