package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCdrDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiConnectorDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCoordinatesDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiEvseDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Profile("mock")
public class MockOcpiClient implements OcpiClient {

    private static final Logger log = LoggerFactory.getLogger(MockOcpiClient.class);

    // =========================================================
    // SANAL TÜKETİM SİMÜLASYONU AYARLARI
    // =========================================================

    // Gerçek saniye cinsinden, kaç saniyede bir tüketim hesabı güncellenir
    private static final long TICK_INTERVAL_SECONDS = 2;

    // Demo hızlı ilerlesin diye: her tick'te "simüle edilmiş" kaç dakika şarj olmuş sayılır
    private static final double SIMULATED_MINUTES_PER_TICK = 6;

    // Dokümandaki 11. madde: provizyona 50 TL kalınca güvenli durdurma
    private static final BigDecimal SAFETY_MARGIN_TL = BigDecimal.valueOf(50);

    private final Map<String, SimulatedSession> activeSessions = new ConcurrentHashMap<>();
    private final Queue<OcpiCdrDto> pendingCdrs = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public MockOcpiClient() {
        scheduler.scheduleAtFixedRate(
                this::tick,
                TICK_INTERVAL_SECONDS,
                TICK_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }

    // =========================================================
    // SESSION İZLEMEYE ALMA (ChargingSessionService çağırır)
    // =========================================================

    /**
     * Gerçek CPO'da bu bilgi CPO'nun kendi sisteminde tutulur.
     * Mock ortamda, session başladığında bu bilgiyi kendimiz saklıyoruz
     * ki periyodik tüketim simülasyonu yapabilelim.
     */
    public void registerSessionForMonitoring(
            String ocpiSessionId,
            BigDecimal requestedAmount,
            BigDecimal unitPrice,
            Integer maxPowerWatt
    ) {
        if (ocpiSessionId == null || requestedAmount == null) {
            return;
        }

        activeSessions.put(
                ocpiSessionId,
                new SimulatedSession(ocpiSessionId, requestedAmount, unitPrice, maxPowerWatt)
        );

        log.info(
                "MOCK CPO: Session {} izlemeye alındı. Provizyon: {} TL",
                ocpiSessionId, requestedAmount
        );
    }

    // =========================================================
    // PERİYODİK TÜKETİM SİMÜLASYONU (11. madde)
    // =========================================================

    private void tick() {
        for (Map.Entry<String, SimulatedSession> entry : activeSessions.entrySet()) {
            SimulatedSession sim = entry.getValue();

            BigDecimal energyThisTick = BigDecimal
                    .valueOf(sim.maxPowerKw * (SIMULATED_MINUTES_PER_TICK / 60.0))
                    .setScale(4, RoundingMode.HALF_UP);

            sim.accumulatedEnergyKwh = sim.accumulatedEnergyKwh.add(energyThisTick);

            BigDecimal currentAmount = sim.accumulatedEnergyKwh
                    .multiply(sim.unitPrice)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal remaining = sim.requestedAmount.subtract(currentAmount);

            boolean requestedAmountTooSmallForMargin =
                    sim.requestedAmount.compareTo(SAFETY_MARGIN_TL) <= 0;

            boolean limitReached = requestedAmountTooSmallForMargin
                    ? currentAmount.compareTo(sim.requestedAmount) >= 0
                    : remaining.compareTo(SAFETY_MARGIN_TL) <= 0;

            if (limitReached) {
                log.info(
                        "MOCK CPO: Session {} provizyon limitine yaklaştı, güvenli şekilde durduruluyor. "
                                + "Tüketim: {} kWh, Tutar: {} TL",
                        sim.ocpiSessionId, sim.accumulatedEnergyKwh, currentAmount
                );

                OcpiCdrDto cdr = new OcpiCdrDto();
                cdr.setId("MOCK-CDR-" + sim.ocpiSessionId);
                cdr.setSessionId(sim.ocpiSessionId);
                cdr.setTotalEnergy(sim.accumulatedEnergyKwh.doubleValue());

                pendingCdrs.add(cdr);
                activeSessions.remove(entry.getKey());
            }
        }
    }

    // =========================================================
    // LOCATIONS (değişmedi)
    // =========================================================

    @Override
    public List<OcpiLocationDto> fetchLocations() {

        OcpiLocationDto location1 = createMockLocation(
                "MOCKLOC1", "Zorlu Center Şarj İstasyonu",
                "Levazım Mahallesi, Vadi Caddesi No:2", "İstanbul", "34340",
                "41.0677", "29.0173"
        );

        OcpiLocationDto location2 = createMockLocation(
                "MOCKLOC2", "İstanbul Havalimanı Şarj İstasyonu",
                "Tayakadın Mahallesi", "İstanbul", "34283",
                "41.2753", "28.7519"
        );

        OcpiLocationDto location6 = createMockLocation(
                "MOCKLOC6", "Kadıköy Şarj İstasyonu",
                "Kadıköy Merkez", "İstanbul", "34710",
                "40.9917", "29.0277"
        );

        OcpiLocationDto testLocation = createMockLocation(
                "MOCKLOCTEST", "Bakırköy Şarj İstasyonu",
                "Bakırköy Merkez", "İstanbul", "34140",
                "40.9760", "28.8721"
        );

        OcpiLocationDto sessionTestLocation = createMockLocation(
                "MOCKLOCSESSIONTEST", "Galataport İstanbul Şarj İstasyonu",
                "Kılıçali Paşa Mahallesi, Meclis-i Mebusan Caddesi No:8", "İstanbul", "34433",
                "41.0256", "28.9840"
        );

        OcpiLocationDto location7 = createMockLocation(
                "MOCKLOCSESSIONTEST3", "Bahçelievler METROPORT AVM İstanbul Şarj İstasyonu",
                "\t Bahçelievler Mahallesi, D-100 Yanyolu, 34180", "İstanbul", "34180",
                "40.5942", "28.5145"
        );

        return List.of(
                location1, location2, location6,
                testLocation, sessionTestLocation, location7
        );
    }

    private OcpiLocationDto createMockLocation(
            String id, String name, String address, String city,
            String postalCode, String latitude, String longitude
    ) {
        OcpiLocationDto location = new OcpiLocationDto();

        location.setId(id);
        location.setCountryCode("TR");
        location.setPartyId("NAF");
        location.setName(name);
        location.setAddress(address);
        location.setCity(city);
        location.setPostalCode(postalCode);
        location.setCountry("TUR");

        OcpiCoordinatesDto coordinates = new OcpiCoordinatesDto();
        coordinates.setLatitude(latitude);
        coordinates.setLongitude(longitude);
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

    // =========================================================
    // CHARGING SESSION
    // =========================================================

    @Override
    public StartSessionResult startSession(String evseUid, String connectorId) {
        String mockSessionId = "MOCK-OCPI-SESSION-" + System.currentTimeMillis();
        return new StartSessionResult(true, mockSessionId);
    }

    @Override
    public void stopSession(String ocpiSessionId) {
        // Mock ortamında gerçek CPO isteği gönderilmiyor.
        // İzlemedeki session'ı da temizleyelim (elle durdurma senaryosu için).
        activeSessions.remove(ocpiSessionId);
    }

    // =========================================================
    // CDR
    // =========================================================

    @Override
    public List<OcpiCdrDto> fetchNewCdrs() {
        List<OcpiCdrDto> result = new ArrayList<>();
        OcpiCdrDto cdr;
        while ((cdr = pendingCdrs.poll()) != null) {
            result.add(cdr);
        }
        return result;
    }

    // =========================================================
    // İÇ SINIF: SANAL SESSION TAKİBİ
    // =========================================================

    private static class SimulatedSession {
        final String ocpiSessionId;
        final BigDecimal requestedAmount;
        final BigDecimal unitPrice;
        final double maxPowerKw;
        BigDecimal accumulatedEnergyKwh = BigDecimal.ZERO;

        SimulatedSession(
                String ocpiSessionId,
                BigDecimal requestedAmount,
                BigDecimal unitPrice,
                Integer maxPowerWatt
        ) {
            this.ocpiSessionId = ocpiSessionId;
            this.requestedAmount = requestedAmount;
            this.unitPrice = (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0)
                    ? BigDecimal.valueOf(8.5)
                    : unitPrice;
            this.maxPowerKw = (maxPowerWatt == null || maxPowerWatt <= 0)
                    ? 22.0
                    : maxPowerWatt / 1000.0;
        }
    }
}