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

        for (OcpiLocationDto ocpiLocation : ocpiLocations) {
            Location location = upsertLocation(ocpiLocation);

            for (OcpiEvseDto ocpiEvse : ocpiLocation.getEvses()) {
                Evse evse = upsertEvse(ocpiEvse, location);

                for (OcpiConnectorDto ocpiConnector : ocpiEvse.getConnectors()) {
                    upsertConnector(ocpiConnector, evse);
                }
            }
        }
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
        try {
            return EvseStatus.valueOf(ocpiStatus);
        } catch (IllegalArgumentException ex) {
            return EvseStatus.UNKNOWN;
        }
    }

    private ConnectorStandard mapConnectorStandard(String ocpiStandard) {
        try {
            return ConnectorStandard.valueOf(ocpiStandard);
        } catch (IllegalArgumentException ex) {
            return ConnectorStandard.IEC_62196_T2; // fallback, gerçek entegrasyonda genişletilecek
        }
    }

    private ConnectorFormat mapConnectorFormat(String ocpiFormat) {
        return ConnectorFormat.valueOf(ocpiFormat);
    }

    private PowerType mapPowerType(String ocpiPowerType) {
        return PowerType.valueOf(ocpiPowerType);
    }
}
