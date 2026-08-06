package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorFormat;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.OcpiClient;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiConnectorDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiEvseDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import java.math.BigDecimal;


@Service
public class OcpiLocationSyncService {

    private final OcpiClient ocpiClient;
    private final LocationRepository locationRepository;
    private final EvseRepository evseRepository;
    private final ConnectorRepository connectorRepository;

    public OcpiLocationSyncService(OcpiClient ocpiClient,
                                    LocationRepository locationRepository,
                                    EvseRepository evseRepository,
                                    ConnectorRepository connectorRepository) {
        this.ocpiClient = ocpiClient;
        this.locationRepository = locationRepository;
        this.evseRepository = evseRepository;
        this.connectorRepository = connectorRepository;
    }

   @Transactional
   public void syncLocations() {

    var ocpiLocations = ocpiClient.fetchLocations();

       // boş response veya null kontrolü bütün lokasyonlar deactive edileceği için kritik. Bu yüzden exception fırlatıyoruz.
        if (ocpiLocations == null) {
        throw new IllegalStateException(
                "OCPI location response is null"
        );
    }

    if (ocpiLocations.isEmpty()) {
        throw new IllegalStateException(
                "OCPI location response is empty. Sync cancelled."
        );
    }

    Set<String> activeOcpiLocationIds = ocpiLocations.stream()
            .map(OcpiLocationDto::getId)
            .collect(Collectors.toSet());

    for (OcpiLocationDto ocpiLocation : ocpiLocations) {

        if (ocpiLocation.getId() == null ||
            ocpiLocation.getId().isBlank()) {

        throw new IllegalStateException(
                "OCPI location ID is missing"
        );
    }

        Location location = upsertLocation(ocpiLocation);

        if (ocpiLocation.getEvses() == null) {
            continue;
    }
        for (OcpiEvseDto ocpiEvse : ocpiLocation.getEvses()) {


              if (ocpiEvse.getUid() == null ||
                    ocpiEvse.getUid().isBlank()) {

                throw new IllegalStateException(
                        "OCPI EVSE UID is missing"
                );
            }


            Evse evse = upsertEvse(ocpiEvse, location);

            if (ocpiEvse.getConnectors() == null) {
            continue;
            }
            for (OcpiConnectorDto ocpiConnector : ocpiEvse.getConnectors()) {

                 if (ocpiConnector.getId() == null ||
                        ocpiConnector.getId().isBlank()) {

                    throw new IllegalStateException(
                            "OCPI connector ID is missing"
                    );
                }
                upsertConnector(ocpiConnector, evse);
            }
        }
    }

    deactivateMissingLocations(activeOcpiLocationIds);
  }

    // ============================
    // Private Methods — Upsert Mantığı
    // ============================

    private Location upsertLocation(OcpiLocationDto dto) {
        Location location = locationRepository.findByOcpiLocationId(dto.getId())
                .orElseGet(Location::new);

        location.setOcpiLocationId(dto.getId());
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setCity(dto.getCity());
        location.setPostalCode(dto.getPostalCode());
        location.setCountry(dto.getCountry());
        location.setLatitude(Double.parseDouble(dto.getCoordinates().getLatitude()));
        location.setLongitude(Double.parseDouble(dto.getCoordinates().getLongitude()));
        location.setActive(true);

        return locationRepository.save(location);
    }

    private Evse upsertEvse(OcpiEvseDto dto, Location location) {
        Evse evse = evseRepository.findByOcpiEvseUid(dto.getUid())
                .orElseGet(Evse::new);

        evse.setOcpiEvseUid(dto.getUid());
        evse.setEvseId(dto.getEvseId());
        evse.setLocation(location);
        evse.setStatus(mapEvseStatus(dto.getStatus()));

        return evseRepository.save(evse);
    }

    private void upsertConnector(OcpiConnectorDto dto, Evse evse) {
        String ocpiConnectorId = evse.getOcpiEvseUid() + "-" + dto.getId();

        Connector connector = connectorRepository.findByOcpiConnectorId(ocpiConnectorId)
                .orElseGet(Connector::new);

        connector.setOcpiConnectorId(ocpiConnectorId);
        connector.setEvse(evse);
        connector.setStandard(mapConnectorStandard(dto.getStandard()));
        connector.setFormat(mapConnectorFormat(dto.getFormat()));
        connector.setPowerType(mapPowerType(dto.getPowerType()));
        connector.setMaxVoltage(dto.getMaxVoltage());
        connector.setMaxAmperage(dto.getMaxAmperage());

        if (connector.getUnitPrice() == null) {
            connector.setUnitPrice(BigDecimal.valueOf(8.5)); // OCPI'de fiyat Tariff modülünde ayrı gelir, şimdilik varsayılan
        }

        connectorRepository.save(connector);
    }

    // ============================
    // Enum Eşleme Metodları
    // ============================

    private EvseStatus mapEvseStatus(String ocpiStatus) {
    if (ocpiStatus == null) return EvseStatus.UNKNOWN;

    return switch (ocpiStatus) {
        case "AVAILABLE" -> EvseStatus.AVAILABLE;
        case "BLOCKED" -> EvseStatus.BLOCKED;
        case "CHARGING" -> EvseStatus.CHARGING;
        case "INOPERATIVE" -> EvseStatus.INOPERATIVE;
        case "OUTOFORDER" -> EvseStatus.OUT_OF_ORDER; // cpo tarafından gelen isim bizim tututuğumuz ile farklı olabilir diye elle eşleştirme yapıyoruz.
        case "PLANNED" -> EvseStatus.PLANNED;
        case "REMOVED" -> EvseStatus.REMOVED;
        case "RESERVED" -> EvseStatus.RESERVED;
        default -> EvseStatus.UNKNOWN;
    };
}
    private ConnectorStandard mapConnectorStandard(String ocpiStandard) {
     if (ocpiStandard == null) return ConnectorStandard.UNKNOWN;
      try {
        return ConnectorStandard.valueOf(ocpiStandard.toUpperCase());
         } catch (IllegalArgumentException ex) {
        return ConnectorStandard.UNKNOWN;
         }
     }

    private ConnectorFormat mapConnectorFormat(String ocpiFormat) {
        return ConnectorFormat.valueOf(ocpiFormat);
    }

    private PowerType mapPowerType(String ocpiPowerType) {
        return PowerType.valueOf(ocpiPowerType);
    }



    private void deactivateMissingLocations(Set<String> activeOcpiLocationIds) {

    var locations = locationRepository.findAll();

    for (Location location : locations) {

        if (!activeOcpiLocationIds.contains(location.getOcpiLocationId())) {

            location.setActive(false);
            location.setDeletedAt(LocalDateTime.now());

            for (Evse evse : location.getEvses()) {

                evse.setDeletedAt(LocalDateTime.now());

                for (Connector connector : evse.getConnectors()) {
                    connector.setDeletedAt(LocalDateTime.now());
                }
            }

            locationRepository.save(location);
        }
    }
}
}
