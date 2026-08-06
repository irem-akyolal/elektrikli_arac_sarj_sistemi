
package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCdrDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiConnectorDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCoordinatesDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiEvseDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.List;

@Component
@Profile("mock")
public class MockOcpiClient implements OcpiClient {

    @Override
    public List<OcpiLocationDto> fetchLocations() {

        // Mevcut mock veriler
        OcpiLocationDto location1 =
                createMockLocation("MOCKLOC1", "Güncellenmiş CPO İstasyonu");

        OcpiLocationDto location2 =
                createMockLocation("MOCKLOC2", "Mock İkinci İstasyon");

        // Scheduler testi için yeni lokasyon
        
        return List.of(location1, location2);
    }

    private OcpiLocationDto createMockLocation(String id, String name) {

        OcpiLocationDto location = new OcpiLocationDto();

        location.setId(id);
        location.setCountryCode("TR");
        location.setPartyId("NAF");
        location.setName(name);
        location.setAddress("Test Caddesi No:1");
        location.setCity("İstanbul");
        location.setPostalCode("34000");
        location.setCountry("TUR");

        OcpiCoordinatesDto coordinates = new OcpiCoordinatesDto();
        coordinates.setLatitude("41.0082");
        coordinates.setLongitude("28.9784");
        location.setCoordinates(coordinates);

        OcpiConnectorDto connector = new OcpiConnectorDto();
        connector.setId("1");
        connector.setStandard("IEC_62196_T2");
        connector.setFormat("SOCKET");
        connector.setPowerType("AC_3_PHASE");
        connector.setMaxVoltage(220);
        connector.setMaxAmperage(32);

        OcpiEvseDto evse = new OcpiEvseDto();
        evse.setUid("MOCK-EVSE-UID-" + id);
        evse.setEvseId("TR*NAF*E" + id);
        evse.setStatus("AVAILABLE");
        evse.setConnectors(List.of(connector));
        evse.setLastUpdated(Instant.now().toString());

        location.setEvses(List.of(evse));
        location.setLastUpdated(Instant.now().toString());

        return location;
    }

    @Override
    public StartSessionResult startSession(String evseUid, String connectorId) {
        String mockSessionId =
                "MOCK-OCPI-SESSION-" + System.currentTimeMillis();

        return new StartSessionResult(true, mockSessionId);
    }

    @Override
    public void stopSession(String ocpiSessionId) {
        // Mock: gerçek bir işlem yapmıyoruz
    }

    @Override
    public List<OcpiCdrDto> fetchNewCdrs() {
    // Gerçek CPO entegrasyonu gelene kadar boş liste döndürüyoruz.
    // Test için OcpiController'daki /cdr/process endpoint'ini kullanacağız.
    return List.of();
    }
}

