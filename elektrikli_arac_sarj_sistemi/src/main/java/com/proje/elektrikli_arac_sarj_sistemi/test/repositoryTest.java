package com.proje.elektrikli_arac_sarj_sistemi.test;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.*;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.*;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class repositoryTest {

    @Bean
    CommandLineRunner testChain(
            LocationRepository locationRepository,
            EvseRepository evseRepository,
            ConnectorRepository connectorRepository
    ) {
        return args -> {
            Location location = new Location();
            location.setOcpiLocationId("TR*TEST*L001");
            location.setName("Test İstasyonu");
            location.setAddress("Test Mahallesi, Test Sokak No:1");
            location.setLatitude(41.0082);
            location.setLongitude(28.9784);
            location.setActive(true);
            locationRepository.save(location);
            System.out.println("Location kaydedildi: " + location.getId());

            Evse evse = new Evse();
            evse.setOcpiEvseUid("TEST-EVSE-UID-001");
            evse.setEvseId("TR*TEST*E001");
            evse.setLocation(location);
            evse.setStatus(EvseStatus.AVAILABLE);
            evseRepository.save(evse);
            System.out.println("Evse kaydedildi: " + evse.getId());

            Connector connector = new Connector();
            connector.setOcpiConnectorId("TR*TEST*C001");
            connector.setEvse(evse);
            connector.setStandard(ConnectorStandard.IEC_62196_T2);
            connector.setFormat(ConnectorFormat.SOCKET);
            connector.setPowerType(PowerType.AC_3_PHASE);
            connector.setUnitPrice(BigDecimal.valueOf(8.5));
            connectorRepository.save(connector);
            System.out.println("Connector kaydedildi: " + connector.getId());

            System.out.println("--- ZİNCİR BAŞARIYLA OLUŞTU ---");
        };
    }
}
