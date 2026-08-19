package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.*;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiConnectorDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiEvseDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OcpiLocationPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(OcpiLocationPersistenceService.class);

    private final LocationRepository locationRepository;
    private final EvseRepository evseRepository;
    private final ConnectorRepository connectorRepository;
    private final ChargingSessionRepository chargingSessionRepository;

    public OcpiLocationPersistenceService(LocationRepository locationRepository,
            EvseRepository evseRepository,
            ConnectorRepository connectorRepository,
            ChargingSessionRepository chargingSessionRepository) {
        this.locationRepository = locationRepository;
        this.evseRepository = evseRepository;
        this.connectorRepository = connectorRepository;
        this.chargingSessionRepository = chargingSessionRepository;

    }

    // KRİTİK: Bu, sadece BU location için AYRI, BAĞIMSIZ bir transaction.
    // Burada bir hata olursa, sadece bu location'ın işlemi rollback olur —
    // diğerleri etkilenmez.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncSingleLocation(OcpiLocationDto dto) {
        if (dto.getId() == null || dto.getId().isBlank()) {
            throw new IllegalStateException("OCPI location ID is missing");
        }

        Location location = upsertLocation(dto);

        if (dto.getEvses() == null) {
            return;
        }

        log.info(
                "Location başarıyla senkronize edildi - OCPI ID: {}",
                dto.getId());

        for (OcpiEvseDto ocpiEvse : dto.getEvses()) {
            if (ocpiEvse.getUid() == null || ocpiEvse.getUid().isBlank()) {
                throw new IllegalStateException("OCPI EVSE UID is missing");
            }

            Evse evse = upsertEvse(ocpiEvse, location);

            if (ocpiEvse.getConnectors() == null) {
                continue;
            }

            for (OcpiConnectorDto ocpiConnector : ocpiEvse.getConnectors()) {
                if (ocpiConnector.getId() == null || ocpiConnector.getId().isBlank()) {
                    throw new IllegalStateException("OCPI connector ID is missing");
                }
                upsertConnector(ocpiConnector, evse);
            }
        }

        log.info(
                "Location başarıyla senkronize edildi - OCPI ID: {}",
                dto.getId());
    }

    // Deactivation da kendi bağımsız transaction'ında — bir location'ın
    // deactivate'i patlarsa diğerleri etkilenmesin
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateLocation(Location location) {

        Location managedLocation = locationRepository
                .findById(location.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Location bulunamadı: " + location.getId()));

        log.info(
                "Location pasif hale getiriliyor - OCPI ID: {}",
                managedLocation.getOcpiLocationId());

        managedLocation.setActive(false);
        managedLocation.setDeletedAt(LocalDateTime.now());

        locationRepository.save(managedLocation);

        for (Evse evse : managedLocation.getEvses()) {

            evse.setDeletedAt(LocalDateTime.now());
            evseRepository.save(evse);

            for (Connector connector : evse.getConnectors()) {

                connector.setDeletedAt(LocalDateTime.now());
                connectorRepository.save(connector);
            }
        }

        log.info(
                "Location pasifleştirildi - OCPI ID: {}",
                managedLocation.getOcpiLocationId());
    }
    // ============================
    // Upsert — değişmedi, aynı mantık
    // ============================

    private Location upsertLocation(OcpiLocationDto dto) {

        Location location = locationRepository
                .findByOcpiLocationId(dto.getId())
                .orElseGet(Location::new);

        boolean isNew = location.getId() == null;

        location.setOcpiLocationId(dto.getId());
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setCity(dto.getCity());
        location.setPostalCode(dto.getPostalCode());
        location.setCountry(dto.getCountry());
        location.setLatitude(
                Double.parseDouble(dto.getCoordinates().getLatitude()));
        location.setLongitude(
                Double.parseDouble(dto.getCoordinates().getLongitude()));

        /*
         * Location CPO'dan ilk kez geliyorsa aktif olarak oluşturulur.
         *
         * Mevcut location'ın active değeri değiştirilmez.
         * Böylece admin tarafından pasifleştirilen location,
         * scheduler tekrar çalıştığında otomatik olarak aktifleşmez.
         */
        if (isNew) {
            location.setActive(true);
        }

        return locationRepository.save(location);
    }

    private Evse upsertEvse(
            OcpiEvseDto dto,
            Location location) {

        Evse evse = evseRepository
                .findByOcpiEvseUid(dto.getUid())
                .orElseGet(Evse::new);

        evse.setOcpiEvseUid(dto.getUid());
        evse.setEvseId(dto.getEvseId());
        evse.setLocation(location);

        /*
         * Yeni EVSE ise henüz bir ChargingSession
         * bulunamaz.
         *
         * Bu durumda OCPI'den gelen status doğrudan
         * kullanılabilir.
         */
        if (evse.getId() == null) {

            evse.setStatus(
                    mapEvseStatus(dto.getStatus()));

            return evseRepository.save(evse);
        }

        /*
         * Mevcut EVSE için aktif bir ChargingSession
         * olup olmadığını kontrol ediyoruz.
         */
        boolean hasActiveSession = chargingSessionRepository
                .existsByConnectorEvseIdAndStatusIn(
                        evse.getId(),
                        List.of(
                                SessionStatus.STARTED,
                                SessionStatus.CHARGING,
                                SessionStatus.COMPLETED));

        /*
         * Aktif session varsa EVSE durumu bizim
         * session lifecycle'ımız tarafından yönetiliyor.
         *
         * OCPI scheduler bu durumu ezmemeli.
         */
        if (!hasActiveSession) {

            evse.setStatus(
                    mapEvseStatus(dto.getStatus()));
        }

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
            connector.setUnitPrice(BigDecimal.valueOf(8.5));
        }

        connectorRepository.save(connector);
    }

    // ============================
    // Enum Eşleme — değişmedi
    // ============================

    private EvseStatus mapEvseStatus(String ocpiStatus) {
        if (ocpiStatus == null)
            return EvseStatus.UNKNOWN;
        return switch (ocpiStatus) {
            case "AVAILABLE" -> EvseStatus.AVAILABLE;
            case "BLOCKED" -> EvseStatus.BLOCKED;
            case "CHARGING" -> EvseStatus.CHARGING;
            case "INOPERATIVE" -> EvseStatus.INOPERATIVE;
            case "OUTOFORDER" -> EvseStatus.OUT_OF_ORDER;
            case "PLANNED" -> EvseStatus.PLANNED;
            case "REMOVED" -> EvseStatus.REMOVED;
            case "RESERVED" -> EvseStatus.RESERVED;
            default -> EvseStatus.UNKNOWN;
        };
    }

    private ConnectorStandard mapConnectorStandard(String ocpiStandard) {
        if (ocpiStandard == null)
            return ConnectorStandard.UNKNOWN;
        try {
            return ConnectorStandard.valueOf(ocpiStandard.toUpperCase());
        } catch (IllegalArgumentException ex) {

            log.warn(
                    "Bilinmeyen OCPI connector standard değeri: {}",
                    ocpiStandard);

            return ConnectorStandard.UNKNOWN;
        }
    }

    private ConnectorFormat mapConnectorFormat(String ocpiFormat) {
        if (ocpiFormat == null)
            return ConnectorFormat.UNKNOWN;
        try {
            return ConnectorFormat.valueOf(ocpiFormat.toUpperCase());
        } catch (IllegalArgumentException ex) {

            log.warn(
                    "Bilinmeyen OCPI connector format değeri: {}",
                    ocpiFormat);

            return ConnectorFormat.UNKNOWN;
        }
    }

    private PowerType mapPowerType(String ocpiPowerType) {
        if (ocpiPowerType == null)
            return PowerType.UNKNOWN;
        try {
            return PowerType.valueOf(ocpiPowerType.toUpperCase());
        } catch (IllegalArgumentException ex) {

            log.warn(
                    "Bilinmeyen OCPI power type değeri: {}",
                    ocpiPowerType);

            return PowerType.UNKNOWN;
        }
    }
}