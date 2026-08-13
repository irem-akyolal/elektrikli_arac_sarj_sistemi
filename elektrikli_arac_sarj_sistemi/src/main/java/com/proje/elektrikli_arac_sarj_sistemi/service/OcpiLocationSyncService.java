package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.OcpiLocationFetcher;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.OcpiLocationPersistenceService;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OcpiLocationSyncService {

    private static final Logger log = LoggerFactory.getLogger(OcpiLocationSyncService.class);

    private final OcpiLocationFetcher fetcher;
    private final OcpiLocationPersistenceService persistenceService;
    private final LocationRepository locationRepository;

    public OcpiLocationSyncService(OcpiLocationFetcher fetcher,
                                    OcpiLocationPersistenceService persistenceService,
                                    LocationRepository locationRepository) {
        this.fetcher = fetcher;
        this.persistenceService = persistenceService;
        this.locationRepository = locationRepository;
    }

    // ARTIK @Transactional DEĞİL — çünkü her location kendi transaction'ında,
    // burada tek bir "şemsiye" transaction olursa yine hepsi birbirini etkiler.
    public void syncLocations() {
        List<OcpiLocationDto> ocpiLocations = fetcher.fetchWithRetry(); // retry + gerçek network hatası burada zaten fırlar

        if (ocpiLocations == null || ocpiLocations.isEmpty()) {
            throw new IllegalStateException("OCPI location response is null or empty. Sync cancelled.");
        }

        Set<String> activeOcpiLocationIds = ocpiLocations.stream()
                .map(OcpiLocationDto::getId)
                .collect(Collectors.toSet());

        int successCount = 0;
        int failCount = 0;

        for (OcpiLocationDto ocpiLocation : ocpiLocations) {
            try {
                persistenceService.syncSingleLocation(ocpiLocation); // her biri KENDİ transaction'ında
                successCount++;
            } catch (Exception ex) {
                failCount++;
                log.error("Location senkronizasyonu başarısız - OCPI ID: {}, Hata: {}",
                        ocpiLocation.getId(), ex.getMessage(), ex);
                // devam ediyoruz, diğer location'lar etkilenmiyor
            }
        }

        deactivateMissingLocations(activeOcpiLocationIds);

        log.info("Senkronizasyon tamamlandı - Başarılı: {}, Başarısız: {}", successCount, failCount);
    }

    private void deactivateMissingLocations(Set<String> activeOcpiLocationIds) {
        List<Location> locations = locationRepository.findAll();

        for (Location location : locations) {
            if (!activeOcpiLocationIds.contains(location.getOcpiLocationId())) {
                try {
                    persistenceService.deactivateLocation(location); // bu da kendi transaction'ında
                } catch (Exception ex) {
                    log.error("Location deactivate edilemedi - ID: {}, Hata: {}",
                            location.getId(), ex.getMessage(), ex);
                }
            }
        }
    }
}




// cpo tarafından gelen isim bizim tututuğumuz ile farklı olabilir diye elle eşleştirme yapıyoruz.