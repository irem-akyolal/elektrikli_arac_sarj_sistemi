package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCdrDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiConnectorDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCoordinatesDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiEvseDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Profile("mock")
public class MockOcpiClient implements OcpiClient {

    @Override
    public List<OcpiLocationDto> fetchLocations() {

        /*
         * Gerçek bir CPO sunucusu olmadığı için
         * test amacıyla gerçek İstanbul bölgelerine
         * yerleştirilmiş mock şarj istasyonları kullanıyoruz.
         *
         * Scheduler bu verileri düzenli olarak çekecek
         * ve veritabanındaki locations tablosuyla
         * senkronize edecek.
         */

        // 1. Zorlu Center - Beşiktaş
        OcpiLocationDto location1 =
                createMockLocation(
                        "MOCKLOC1",
                        "Zorlu Center Şarj İstasyonu",
                        "Levazım Mahallesi, Vadi Caddesi No:2",
                        "İstanbul",
                        "34340",
                        "41.0677",
                        "29.0173"
                );

        // 2. İstanbul Havalimanı
        OcpiLocationDto location2 =
                createMockLocation(
                        "MOCKLOC2",
                        "İstanbul Havalimanı Şarj İstasyonu",
                        "Tayakadın Mahallesi",
                        "İstanbul",
                        "34283",
                        "41.2753",
                        "28.7519"
                );

        // 3. Kadıköy
        OcpiLocationDto location6 =
                createMockLocation(
                        "MOCKLOC6",
                        "Kadıköy Şarj İstasyonu",
                        "Kadıköy Merkez",
                        "İstanbul",
                        "34710",
                        "40.9917",
                        "29.0277"
                );

        // 4. Bakırköy
        OcpiLocationDto testLocation =
                createMockLocation(
                        "MOCKLOCTEST",
                        "Bakırköy Şarj İstasyonu",
                        "Bakırköy Merkez",
                        "İstanbul",
                        "34140",
                        "40.9760",
                        "28.8721"
                );

        return List.of(
                location1,
                location2,
                location6,
                testLocation
        );
    }

    /**
     * Mock Location oluşturur.
     */
    private OcpiLocationDto createMockLocation(
            String id,
            String name,
            String address,
            String city,
            String postalCode,
            String latitude,
            String longitude
    ) {

        OcpiLocationDto location = new OcpiLocationDto();

        // =========================
        // LOCATION BİLGİLERİ
        // =========================

        location.setId(id);
        location.setCountryCode("TR");
        location.setPartyId("NAF");

        location.setName(name);
        location.setAddress(address);
        location.setCity(city);
        location.setPostalCode(postalCode);
        location.setCountry("TUR");

        // =========================
        // KOORDİNATLAR
        // =========================

        OcpiCoordinatesDto coordinates =
                new OcpiCoordinatesDto();

        coordinates.setLatitude(latitude);
        coordinates.setLongitude(longitude);

        location.setCoordinates(coordinates);

        // =========================
        // CONNECTOR
        // =========================

        OcpiConnectorDto connector =
                new OcpiConnectorDto();

        connector.setId("1");
        connector.setStandard("IEC_62196_T2");
        connector.setFormat("SOCKET");
        connector.setPowerType("AC_3_PHASE");

        connector.setMaxVoltage(220);
        connector.setMaxAmperage(32);

        // =========================
        // EVSE
        // =========================

        OcpiEvseDto evse =
                new OcpiEvseDto();

        evse.setUid(
                "MOCK-EVSE-UID-" + id
        );

        evse.setEvseId(
                "TR*NAF*E" + id
        );

        evse.setStatus("AVAILABLE");

        evse.setConnectors(
                List.of(connector)
        );

        evse.setLastUpdated(
                Instant.now().toString()
        );

        // =========================
        // LOCATION → EVSE
        // =========================

        location.setEvses(
                List.of(evse)
        );

        location.setLastUpdated(
                Instant.now().toString()
        );

        return location;
    }

    // =========================================================
    // CHARGING SESSION
    // =========================================================

    @Override
    public StartSessionResult startSession(
            String evseUid,
            String connectorId
    ) {

        String mockSessionId =
                "MOCK-OCPI-SESSION-"
                        + System.currentTimeMillis();

        return new StartSessionResult(
                true,
                mockSessionId
        );
    }

    // =========================================================
    // STOP CHARGING SESSION
    // =========================================================

    @Override
    public void stopSession(
            String ocpiSessionId
    ) {

        // Mock ortamında gerçek CPO isteği gönderilmiyor.
    }

    // =========================================================
    // CDR
    // =========================================================

    @Override
    public List<OcpiCdrDto> fetchNewCdrs() {

        /*
         * Gerçek CPO entegrasyonu olmadığı için
         * şimdilik yeni CDR döndürmüyoruz.
         *
         * İleride gerçek CPO entegrasyonu
         * yapıldığında burada CDR verileri alınacak.
         */

        return List.of();
    }
}
